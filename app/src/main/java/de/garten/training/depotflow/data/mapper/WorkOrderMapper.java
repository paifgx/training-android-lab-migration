package de.garten.training.depotflow.data.mapper;

import de.garten.training.depotflow.data.api.dto.WorkOrderDto;
import de.garten.training.depotflow.data.db.green.WorkOrder;
import de.garten.training.depotflow.domain.SyncStatus;
import de.garten.training.depotflow.domain.WorkOrderStatus;

public class WorkOrderMapper {

    public WorkOrder fromDto(WorkOrderDto dto) {
        WorkOrder entity = new WorkOrder();
        entity.setServerId(dto.id);
        entity.setExternalNumber(dto.externalNumber);
        entity.setTitle(dto.title);
        entity.setCustomerName(dto.customerName);
        entity.setStatus(WorkOrderStatus.fromServerValue(dto.status));
        entity.setPriority(dto.priority);
        entity.setDueAt(dto.dueAt);
        entity.setUpdatedAt(dto.updatedAt);
        entity.setAssignedUser(dto.assignedUser);
        entity.setSyncStatus(SyncStatus.CLEAN);
        entity.setDirty(false);
        entity.setLastError(null);
        return entity;
    }

    public String toDisplayTitle(WorkOrder entity) {
        if (entity.getExternalNumber() == null || entity.getExternalNumber().isEmpty()) {
            return entity.getTitle();
        }
        return entity.getExternalNumber() + " · " + entity.getTitle();
    }

    public boolean shouldOverwriteLocal(WorkOrder local, WorkOrderDto remote) {
        if (local == null) {
            return true;
        }
        if (local.isDirty()) {
            return false;
        }
        if (remote.updatedAt == null) {
            return false;
        }
        return local.getUpdatedAt() == null || remote.updatedAt.compareTo(local.getUpdatedAt()) >= 0;
    }
}
