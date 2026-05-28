package de.garten.training.depotflow.data.api.dto

class StopDto {
    @JvmField
    var id: String? = null

    @JvmField
    var sequence: Int = 0

    @JvmField
    var type: String? = null

    @JvmField
    var name: String? = null

    @JvmField
    var address: String? = null

    @JvmField
    var latitude: Double = 0.0

    @JvmField
    var longitude: Double = 0.0

    @JvmField
    var status: String? = null

    @JvmField
    var arrivalWindowFrom: String? = null

    @JvmField
    var arrivalWindowTo: String? = null
}
