package de.garten.training.depotflow.data.api.dto

class WorkOrderDto {
    var id: String? = null
    var externalNumber: String? = null
    var title: String? = null
    var customerName: String? = null
    var status: String? = null
    var priority: Int = 0
    var dueAt: String? = null
    var updatedAt: String? = null
    var assignedUser: String? = null
    var stops: MutableList<StopDto?>? = null
    var checklist: MutableList<ChecklistItemDto?>? = null

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
