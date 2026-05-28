package de.garten.training.depotflow.data.api.dto

class StopDto {
    var id: String? = null
    var sequence: Int = 0
    var type: String? = null
    var name: String? = null
    var address: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var status: String? = null
    var arrivalWindowFrom: String? = null
    var arrivalWindowTo: String? = null
}
