package de.garten.training.depotflow.data.api.dto;

public class OutboxDto {
    public String aggregateType;
    public String aggregateId;
    public String operation;
    public String payload;
    public String createdAt;

    public OutboxDto() {
    }

    public OutboxDto(String aggregateType, String aggregateId, String operation, String payload, String createdAt) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.operation = operation;
        this.payload = payload;
        this.createdAt = createdAt;
    }
}
