package de.garten.training.depotflow.data.mapper;

import de.garten.training.depotflow.data.api.dto.OutboxDto;
import de.garten.training.depotflow.data.db.green.SyncOutboxEntry;

public class OutboxMapper {

    public OutboxDto toDto(SyncOutboxEntry entry) {
        return new OutboxDto(
                entry.getAggregateType(),
                entry.getAggregateId(),
                entry.getOperation(),
                entry.getPayload(),
                entry.getCreatedAt()
        );
    }
}
