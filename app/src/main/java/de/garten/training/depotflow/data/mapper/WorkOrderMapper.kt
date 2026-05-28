package de.garten.training.depotflow.data.mapper

import de.garten.training.depotflow.data.api.dto.WorkOrderDto
import de.garten.training.depotflow.data.db.green.WorkOrder
import de.garten.training.depotflow.domain.SyncStatus
import de.garten.training.depotflow.domain.WorkOrderStatus

class WorkOrderMapper {
    fun fromDto(dto: WorkOrderDto): WorkOrder {
        return WorkOrder().apply {
            serverId = dto.id
            externalNumber = dto.externalNumber
            title = dto.title
            customerName = dto.customerName
            status = WorkOrderStatus.fromServerValue(dto.status)
            priority = dto.priority
            dueAt = dto.dueAt
            updatedAt = dto.updatedAt
            assignedUser = dto.assignedUser
            syncStatus = SyncStatus.CLEAN
            isDirty = false
            lastError = null
        }
    }

    fun toDisplayTitle(entity: WorkOrder): String? {
        val externalNumber = entity.externalNumber
        if (externalNumber.isNullOrEmpty()) {
            return entity.title
        }
        return "$externalNumber · ${entity.title}"
    }

    fun shouldOverwriteLocal(local: WorkOrder?, remote: WorkOrderDto): Boolean {
        if (local == null) {
            return true
        }
        if (local.isDirty) {
            return false
        }

        val remoteUpdatedAt = remote.updatedAt ?: return false
        val localUpdatedAt = local.updatedAt
        return localUpdatedAt == null || remoteUpdatedAt >= localUpdatedAt
    }
}
