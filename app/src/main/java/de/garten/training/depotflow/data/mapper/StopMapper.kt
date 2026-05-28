package de.garten.training.depotflow.data.mapper

import de.garten.training.depotflow.data.api.dto.StopDto
import de.garten.training.depotflow.data.db.green.Stop
import de.garten.training.depotflow.domain.StopType

class StopMapper {
    fun fromDto(dto: StopDto): Stop {
        return Stop().apply {
            remoteId = dto.id
            sequence = dto.sequence
            type = StopType.fromDatabaseValue(dto.type)
            name = dto.name
            address = dto.address
            latitude = dto.latitude
            longitude = dto.longitude
            status = dto.status
            arrivalWindowFrom = dto.arrivalWindowFrom
            arrivalWindowTo = dto.arrivalWindowTo
        }
    }
}
