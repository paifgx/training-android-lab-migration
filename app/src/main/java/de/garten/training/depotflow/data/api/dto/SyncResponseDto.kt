package de.garten.training.depotflow.data.api.dto

class SyncResponseDto {
    @JvmField
    var success: Boolean = false

    @JvmField
    var acceptedAggregateIds: List<String>? = null

    @JvmField
    var rejectedAggregateIds: List<String>? = null

    @JvmField
    var serverTime: String? = null
}
