package de.garten.training.depotflow.data.api.dto

class OutboxDto {
    @JvmField
    var aggregateType: String? = null

    @JvmField
    var aggregateId: String? = null

    @JvmField
    var operation: String? = null

    @JvmField
    var payload: String? = null

    @JvmField
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
