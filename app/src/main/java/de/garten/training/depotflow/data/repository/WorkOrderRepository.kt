package de.garten.training.depotflow.data.repository

import com.google.gson.Gson
import de.garten.training.depotflow.core.DateTimeProvider
import de.garten.training.depotflow.core.LegacyExecutors
import de.garten.training.depotflow.core.LegacyLogger
import de.garten.training.depotflow.core.ResultCallback
import de.garten.training.depotflow.data.api.DepotApiClient
import de.garten.training.depotflow.data.api.dto.WorkOrderDto
import de.garten.training.depotflow.data.db.LegacyDatabase
import de.garten.training.depotflow.data.db.green.SyncOutboxEntry
import de.garten.training.depotflow.data.db.green.WorkOrder
import de.garten.training.depotflow.data.mapper.ChecklistMapper
import de.garten.training.depotflow.data.mapper.OutboxMapper
import de.garten.training.depotflow.data.mapper.StopMapper
import de.garten.training.depotflow.data.mapper.WorkOrderMapper
import de.garten.training.depotflow.domain.WorkOrderStatus

class WorkOrderRepository(
    private val database: LegacyDatabase,
    private val apiClient: DepotApiClient,
    private val executors: LegacyExecutors,
    private val dateTimeProvider: DateTimeProvider
) {
    private val workOrderMapper = WorkOrderMapper()
    private val stopMapper = StopMapper()
    private val checklistMapper = ChecklistMapper()
    private val outboxMapper = OutboxMapper()
    private val gson = Gson()

    fun loadLocalOrders(callback: ResultCallback<List<WorkOrder>>) {
        executors.disk {
            try {
                val orders = database.workOrderDao().loadAll()
                deliverSuccess(callback, orders)
            } catch (error: Throwable) {
                deliverError(callback, error)
            }
        }
    }

    fun refreshOrders(callback: ResultCallback<List<WorkOrder>>) {
        apiClient.loadWorkOrders(
            DEFAULT_DEPOT_ID,
            null,
            object : ResultCallback<List<WorkOrderDto>> {
                override fun onSuccess(remoteOrders: List<WorkOrderDto>) {
                    executors.disk {
                        try {
                            remoteOrders.forEach(::saveRemoteOrder)
                            val orders = database.workOrderDao().loadAll()
                            deliverSuccess(callback, orders)
                        } catch (error: Throwable) {
                            deliverError(callback, error)
                        }
                    }
                }

                override fun onError(error: Throwable) {
                    LegacyLogger.e("repo", "refresh failed", error)
                    deliverError(callback, error)
                }
            }
        )
    }

    fun loadDetails(localId: Long, callback: ResultCallback<WorkOrderDetails>) {
        executors.disk {
            try {
                val order = requireNotNull(database.workOrderDao().load(localId)) {
                    "Unknown work order id $localId"
                }
                val stops = database.stopDao().loadForWorkOrder(localId)
                val items = database.checklistItemDao().loadForWorkOrder(localId)
                deliverSuccess(callback, WorkOrderDetails(order, stops, items))
            } catch (error: Throwable) {
                deliverError(callback, error)
            }
        }
    }

    fun completeOffline(serverId: String, callback: ResultCallback<WorkOrder>) {
        executors.disk {
            try {
                requireNotNull(database.workOrderDao().loadByServerId(serverId)) {
                    "Unknown work order $serverId"
                }

                val updatedAt = dateTimeProvider.nowIsoUtc()
                database.workOrderDao().updateLocalStatus(serverId, WorkOrderStatus.DONE, updatedAt)

                val outboxEntry = createCompletionOutboxEntry(serverId, updatedAt)
                database.syncOutboxEntryDao().insert(outboxEntry)

                val after = requireNotNull(database.workOrderDao().loadByServerId(serverId)) {
                    "Completed work order disappeared after local update: $serverId"
                }

                // Legacy smell: repository triggers an optimistic network call although the method name says offline.
                completeRemoteOptimistically(serverId, outboxEntry)

                deliverSuccess(callback, after)
            } catch (error: Throwable) {
                deliverError(callback, error)
            }
        }
    }

    private fun createCompletionOutboxEntry(serverId: String, updatedAt: String): SyncOutboxEntry {
        return SyncOutboxEntry().apply {
            aggregateType = AGGREGATE_TYPE_WORK_ORDER
            aggregateId = serverId
            operation = OPERATION_COMPLETE
            createdAt = updatedAt
            attempts = 0
            payload = gson.toJson(CompletionPayload(serverId, updatedAt, SOURCE_ANDROID_LEGACY))
        }
    }

    private fun completeRemoteOptimistically(serverId: String, outboxEntry: SyncOutboxEntry) {
        apiClient.completeWorkOrder(
            serverId,
            outboxMapper.toDto(outboxEntry),
            object : ResultCallback<WorkOrderDto> {
                override fun onSuccess(remote: WorkOrderDto) {
                    executors.disk {
                        try {
                            saveRemoteOrder(remote)
                            database.workOrderDao().markClean(serverId, remote.updatedAt)
                        } catch (error: Throwable) {
                            LegacyLogger.e("repo", "remote completion merge failed", error)
                        }
                    }
                }

                override fun onError(error: Throwable) {
                    LegacyLogger.e(
                        "repo",
                        "optimistic completion failed; outbox keeps pending change",
                        error
                    )
                }
            }
        )
    }

    private fun saveRemoteOrder(dto: WorkOrderDto) {
        val remoteId = requireNotNull(dto.id) { "Remote work order has no id" }
        val local = database.workOrderDao().loadByServerId(remoteId)
        if (!workOrderMapper.shouldOverwriteLocal(local, dto)) {
            return
        }

        val entity = workOrderMapper.fromDto(dto).apply {
            if (local != null) {
                id = local.id
            }
        }
        val localId = database.workOrderDao().insertOrReplace(entity)

        val stops = dto.stops
            ?.map(stopMapper::fromDto)
            .orEmpty()
        database.stopDao().replaceForWorkOrder(localId, stops)

        val checklist = dto.checklist
            ?.map(checklistMapper::fromDto)
            .orEmpty()
        database.checklistItemDao().replaceForWorkOrder(localId, checklist)
    }

    private fun <T> deliverError(callback: ResultCallback<T>, error: Throwable) {
        executors.main { callback.onError(error) }
    }

    private fun <T> deliverSuccess(callback: ResultCallback<T>, value: T) {
        executors.main { callback.onSuccess(value) }
    }

    private data class CompletionPayload(
        val serverId: String,
        val completedAt: String,
        val source: String
    )

    companion object {
        private const val DEFAULT_DEPOT_ID = "north-01"
        private const val AGGREGATE_TYPE_WORK_ORDER = "WORK_ORDER"
        private const val OPERATION_COMPLETE = "COMPLETE"
        private const val SOURCE_ANDROID_LEGACY = "android-legacy"
    }
}
