package de.garten.training.depotflow.data.mapper

import de.garten.training.depotflow.data.api.dto.OutboxDto
import de.garten.training.depotflow.data.db.green.SyncOutboxEntry

class OutboxMapper {
    fun toDto(entry: SyncOutboxEntry): OutboxDto {
        return OutboxDto(
            entry.getAggregateType(),
            entry.getAggregateId(),
            entry.getOperation(),
            entry.getPayload(),
            entry.getCreatedAt()
        )
    }
}
