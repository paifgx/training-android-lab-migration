package de.garten.training.depotflow.data.db.green;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "CHECKLIST_ITEM")
public class ChecklistItem {
    @Id
    private Long id;

    @Property(nameInDb = "WORK_ORDER_ID")
    private long workOrderId;

    @Property(nameInDb = "REMOTE_ID")
    private String remoteId;

    @Property(nameInDb = "LABEL")
    private String label;

    @Property(nameInDb = "IS_CHECKED")
    private boolean checked;

    @Property(nameInDb = "IS_MANDATORY")
    private boolean mandatory;

    @Property(nameInDb = "NOTE")
    private String note;

    public ChecklistItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
