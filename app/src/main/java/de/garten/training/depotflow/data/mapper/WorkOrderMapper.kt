package de.garten.training.depotflow.data.mapper

import de.garten.training.depotflow.data.api.dto.WorkOrderDto
import de.garten.training.depotflow.data.db.green.WorkOrder
import de.garten.training.depotflow.domain.SyncStatus
import de.garten.training.depotflow.domain.WorkOrderStatus

class WorkOrderMapper {
    fun fromDto(dto: WorkOrderDto): WorkOrder {
        val entity = WorkOrder()
        entity.setServerId(dto.id)
        entity.setExternalNumber(dto.externalNumber)
        entity.setTitle(dto.title)
        entity.setCustomerName(dto.customerName)
        entity.setStatus(WorkOrderStatus.fromServerValue(dto.status))
        entity.setPriority(dto.priority)
        entity.setDueAt(dto.dueAt)
        entity.setUpdatedAt(dto.updatedAt)
        entity.setAssignedUser(dto.assignedUser)
        entity.setSyncStatus(SyncStatus.CLEAN)
        entity.setDirty(false)
        entity.setLastError(null)
        return entity
    }

    fun toDisplayTitle(entity: WorkOrder): String? {
        if (entity.getExternalNumber() == null || entity.getExternalNumber().isEmpty()) {
            return entity.getTitle()
        }
        return entity.getExternalNumber() + " · " + entity.getTitle()
    }

    fun shouldOverwriteLocal(local: WorkOrder?, remote: WorkOrderDto): Boolean {
        if (local == null) {
            return true
        }
        if (local.isDirty()) {
            return false
        }
        if (remote.updatedAt == null) {
            return false
        }
        return local.getUpdatedAt() == null || remote.updatedAt!!.compareTo(local.getUpdatedAt()) >= 0
    }
}
