package de.garten.training.depotflow.data.api.dto

class WorkOrderDto {
    @JvmField
    var id: String? = null

    @JvmField
    var externalNumber: String? = null

    @JvmField
    var title: String? = null

    @JvmField
    var customerName: String? = null

    @JvmField
    var status: String? = null

    @JvmField
    var priority: Int = 0

    @JvmField
    var dueAt: String? = null

    @JvmField
    var updatedAt: String? = null

    @JvmField
    var assignedUser: String? = null

    @JvmField
    var stops: List<StopDto>? = null

    @JvmField
    var checklist: List<ChecklistItemDto>? = null

    constructor()

    constructor(
        id: String?,
        externalNumber: String?,
        title: String?,
        customerName: String?,
        status: String?,
        priority: Int
    ) {
        this.id = id
        this.externalNumber = externalNumber
        this.title = title
        this.customerName = customerName
        this.status = status
        this.priority = priority
    }
}
