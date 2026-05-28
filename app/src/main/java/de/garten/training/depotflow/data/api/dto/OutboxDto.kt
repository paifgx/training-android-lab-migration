package de.garten.training.depotflow.data.api.dto

class OutboxDto {
    var aggregateType: String? = null
    var aggregateId: String? = null
    var operation: String? = null
    var payload: String? = null
    var createdAt: String? = null

    constructor()

    constructor(
        aggregateType: String?,
        aggregateId: String?,
        operation: String?,
        payload: String?,
        createdAt: String?
    ) {
        this.aggregateType = aggregateType
        this.aggregateId = aggregateId
        this.operation = operation
        this.payload = payload
        this.createdAt = createdAt
    }
}
