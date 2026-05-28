package de.garten.training.depotflow.data.api.dto

class SyncResponseDto {
    var success: Boolean = false
    var acceptedAggregateIds: MutableList<String?>? = null
    var rejectedAggregateIds: MutableList<String?>? = null
    var serverTime: String? = null
}
