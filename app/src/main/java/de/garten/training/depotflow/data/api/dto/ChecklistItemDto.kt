package de.garten.training.depotflow.data.api.dto

class ChecklistItemDto {
    @JvmField
    var id: String? = null

    @JvmField
    var label: String? = null

    @JvmField
    var checked: Boolean = false

    @JvmField
    var mandatory: Boolean = false

    @JvmField
    var note: String? = null
}
