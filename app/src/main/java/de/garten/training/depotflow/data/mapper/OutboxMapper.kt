package de.garten.training.depotflow.data.mapper

import de.garten.training.depotflow.data.api.dto.OutboxDto
import de.garten.training.depotflow.data.db.green.SyncOutboxEntry

class OutboxMapper {
    fun toDto(entry: SyncOutboxEntry): OutboxDto {
        return OutboxDto(
            entry.aggregateType,
            entry.aggregateId,
            entry.operation,
            entry.payload,
            entry.createdAt
        )
    }
}
