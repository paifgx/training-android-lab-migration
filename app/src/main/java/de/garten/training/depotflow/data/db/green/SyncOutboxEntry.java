package de.garten.training.depotflow.data.db.green;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "SYNC_OUTBOX")
public class SyncOutboxEntry {
    @Id
    private Long id;

    @Property(nameInDb = "AGGREGATE_TYPE")
    private String aggregateType;

    @Property(nameInDb = "AGGREGATE_ID")
    private String aggregateId;

    @Property(nameInDb = "OPERATION")
    private String operation;

    @Property(nameInDb = "PAYLOAD")
    private String payload;

    @Property(nameInDb = "CREATED_AT")
    private String createdAt;

    @Property(nameInDb = "ATTEMPTS")
    private int attempts;

    @Property(nameInDb = "LAST_ERROR")
    private String lastError;

    public SyncOutboxEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
}
