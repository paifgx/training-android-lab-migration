package de.garten.training.depotflow.data.repository

import de.garten.training.depotflow.core.DateTimeProvider
import de.garten.training.depotflow.core.LegacyExecutors
import de.garten.training.depotflow.core.ResultCallback
import de.garten.training.depotflow.data.api.DepotApiClient
import de.garten.training.depotflow.data.api.dto.SyncResponseDto
import de.garten.training.depotflow.data.db.LegacyDatabase
import de.garten.training.depotflow.data.db.green.SyncOutboxEntry
import de.garten.training.depotflow.data.mapper.OutboxMapper

class SyncRepository(
    private val database: LegacyDatabase,
    private val apiClient: DepotApiClient,
    private val executors: LegacyExecutors,
    private val dateTimeProvider: DateTimeProvider
) {
    private val outboxMapper = OutboxMapper()

    fun loadSummary(callback: ResultCallback<String>) {
        executors.disk {
            try {
                val pending = database.syncOutboxEntryDao().countPending()
                val dirty = database.workOrderDao().queryDirty().size
                val summary = """
                    Ausstehende Outbox-Einträge: $pending
                    Dirty Work Orders: $dirty
                    Letzter lokaler Check: ${dateTimeProvider.nowIsoUtc()}
                """.trimIndent()

                deliverSuccess(callback, summary)
            } catch (error: Throwable) {
                deliverError(callback, error)
            }
        }
    }

    fun pushPending(callback: ResultCallback<String>) {
        executors.disk {
            val pending = database.syncOutboxEntryDao().loadPending()
            if (pending.isEmpty()) {
                deliverSuccess(callback, "Keine lokalen Änderungen vorhanden.")
                return@disk
            }

            val payload = pending.map(outboxMapper::toDto)
            apiClient.pushOutbox(payload, createPushCallback(pending, callback))
        }
    }

    private fun createPushCallback(
        pending: List<SyncOutboxEntry>,
        callback: ResultCallback<String>
    ): ResultCallback<SyncResponseDto> {
        return object : ResultCallback<SyncResponseDto> {
            override fun onSuccess(response: SyncResponseDto) {
                executors.disk {
                    try {
                        markAcceptedEntriesClean(pending, response)
                        deliverSuccess(callback, "Sync abgeschlossen. Serverzeit: ${response.serverTime}")
                    } catch (error: Throwable) {
                        deliverError(callback, error)
                    }
                }
            }

            override fun onError(error: Throwable) {
                executors.disk {
                    try {
                        markPendingEntriesFailed(pending, error)
                    } finally {
                        deliverError(callback, error)
                    }
                }
            }
        }
    }

    private fun markAcceptedEntriesClean(pending: List<SyncOutboxEntry>, response: SyncResponseDto) {
        pending
            .filter { entry -> response.accepts(entry) }
            .forEach { entry ->
                database.syncOutboxEntryDao().delete(entry.requireId())
                database.workOrderDao().markClean(entry.requireAggregateId(), response.serverTime)
            }
    }

    private fun markPendingEntriesFailed(pending: List<SyncOutboxEntry>, error: Throwable) {
        pending.forEach { entry ->
            database.syncOutboxEntryDao().increaseAttempts(entry.requireId(), error.message)
            database.workOrderDao().markSyncFailed(entry.requireAggregateId(), error.message)
        }
    }

    private fun SyncResponseDto.accepts(entry: SyncOutboxEntry): Boolean {
        if (success) {
            return true
        }

        val acceptedIds = acceptedAggregateIds ?: return true
        val aggregateId = entry.aggregateId ?: return false
        return aggregateId in acceptedIds
    }

    private fun SyncOutboxEntry.requireId(): Long {
        return requireNotNull(id) { "Outbox entry has no local id" }
    }

    private fun SyncOutboxEntry.requireAggregateId(): String {
        return requireNotNull(aggregateId) { "Outbox entry has no aggregate id" }
    }

    private fun deliverSuccess(callback: ResultCallback<String>, message: String) {
        executors.main { callback.onSuccess(message) }
    }

    private fun deliverError(callback: ResultCallback<*>, error: Throwable) {
        executors.main { callback.onError(error) }
    }
}
