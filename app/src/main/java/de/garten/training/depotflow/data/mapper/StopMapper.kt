package de.garten.training.depotflow.data.mapper

import de.garten.training.depotflow.data.api.dto.StopDto
import de.garten.training.depotflow.data.db.green.Stop
import de.garten.training.depotflow.domain.StopType

class StopMapper {
    fun fromDto(dto: StopDto): Stop {
        val stop = Stop()
        stop.setRemoteId(dto.id)
        stop.setSequence(dto.sequence)
        stop.setType(StopType.fromDatabaseValue(dto.type))
        stop.setName(dto.name)
        stop.setAddress(dto.address)
        stop.setLatitude(dto.latitude)
        stop.setLongitude(dto.longitude)
        stop.setStatus(dto.status)
        stop.setArrivalWindowFrom(dto.arrivalWindowFrom)
        stop.setArrivalWindowTo(dto.arrivalWindowTo)
        return stop
    }
}
