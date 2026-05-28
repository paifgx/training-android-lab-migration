package de.garten.training.depotflow.data.db.green;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

import de.garten.training.depotflow.data.db.converter.SyncStatusConverter;
import de.garten.training.depotflow.data.db.converter.WorkOrderStatusConverter;
import de.garten.training.depotflow.domain.SyncStatus;
import de.garten.training.depotflow.domain.WorkOrderStatus;

@Entity(nameInDb = "WORK_ORDER")
public class WorkOrder {
    @Id
    private Long id;

    @Unique
    @Property(nameInDb = "SERVER_ID")
    private String serverId;

    @Property(nameInDb = "EXTERNAL_NUMBER")
    private String externalNumber;

    @Property(nameInDb = "TITLE")
    private String title;

    @Property(nameInDb = "CUSTOMER_NAME")
    private String customerName;

    @Convert(converter = WorkOrderStatusConverter.class, columnType = String.class)
    @Property(nameInDb = "STATUS")
    private WorkOrderStatus status;

    @Property(nameInDb = "PRIORITY")
    private int priority;

    @Property(nameInDb = "DUE_AT")
    private String dueAt;

    @Property(nameInDb = "UPDATED_AT")
    private String updatedAt;

    @Property(nameInDb = "ASSIGNED_USER")
    private String assignedUser;

    @Convert(converter = SyncStatusConverter.class, columnType = String.class)
    @Property(nameInDb = "SYNC_STATUS")
    private SyncStatus syncStatus;

    @Property(nameInDb = "LAST_ERROR")
    private String lastError;

    @Property(nameInDb = "DIRTY")
    private boolean dirty;

    public WorkOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getExternalNumber() {
        return externalNumber;
    }

    public void setExternalNumber(String externalNumber) {
        this.externalNumber = externalNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDueAt() {
        return dueAt;
    }

    public void setDueAt(String dueAt) {
        this.dueAt = dueAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
