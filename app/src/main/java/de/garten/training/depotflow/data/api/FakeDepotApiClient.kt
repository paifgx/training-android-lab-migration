package de.garten.training.depotflow.data.api

import de.garten.training.depotflow.core.ResultCallback
import de.garten.training.depotflow.data.api.dto.ChecklistItemDto
import de.garten.training.depotflow.data.api.dto.OutboxDto
import de.garten.training.depotflow.data.api.dto.StopDto
import de.garten.training.depotflow.data.api.dto.SyncResponseDto
import de.garten.training.depotflow.data.api.dto.WorkOrderDto

class FakeDepotApiClient : DepotApiClient {

    override fun loadWorkOrders(
        depotId: String,
        changedSince: String?,
        callback: ResultCallback<List<WorkOrderDto>>,
    ) {
        callback.onSuccess(
            listOf(
                order(
                    id = "wo-1001",
                    externalNumber = "DF-2026-1001",
                    title = "Torprüfung und Scanner-Tausch",
                    customer = "Norddepot Cloppenburg",
                    status = "accepted",
                    priority = 8,
                    user = "m.schneider",
                ),
                order(
                    id = "wo-1002",
                    externalNumber = "DF-2026-1002",
                    title = "Ersatzteile an Außenlager liefern",
                    customer = "Servicepunkt Bremen",
                    status = "in_progress",
                    priority = 5,
                    user = "l.koenig",
                ),
                order(
                    id = "wo-1003",
                    externalNumber = "DF-2026-1003",
                    title = "Temperatursensor im Außenlager prüfen",
                    customer = "Kühlbereich Oldenburg",
                    status = "new",
                    priority = 9,
                    user = "a.weber",
                ),
            ),
        )
    }

    override fun loadWorkOrder(serverId: String, callback: ResultCallback<WorkOrderDto>) {
        callback.onSuccess(
            order(
                id = serverId,
                externalNumber = "DF-2026-DEMO",
                title = "Remote-Detail nachladen",
                customer = "Demo-Depot",
                status = "accepted",
                priority = 3,
                user = "demo.user",
            ),
        )
    }

    override fun completeWorkOrder(
        serverId: String,
        payload: OutboxDto,
        callback: ResultCallback<WorkOrderDto>,
    ) {
        callback.onSuccess(
            order(
                id = serverId,
                externalNumber = "DF-2026-COMPLETE",
                title = "Lokal erledigter Auftrag",
                customer = "Demo-Depot",
                status = "done",
                priority = 1,
                user = "demo.user",
            ).apply {
                updatedAt = "2026-05-27T14:40:00Z"
            },
        )
    }

    override fun pushOutbox(
        payload: List<OutboxDto>,
        callback: ResultCallback<SyncResponseDto>,
    ) {
        callback.onSuccess(
            SyncResponseDto().apply {
                success = true
                acceptedAggregateIds = payload.mapNotNull { it.aggregateId }.toMutableList()
                rejectedAggregateIds = mutableListOf()
                serverTime = "2026-05-27T14:45:00Z"
            },
        )
    }

    private fun order(
        id: String,
        externalNumber: String,
        title: String,
        customer: String,
        status: String,
        priority: Int,
        user: String,
    ) = WorkOrderDto(
        id = id,
        externalNumber = externalNumber,
        title = title,
        customerName = customer,
        status = status,
        priority = priority,
    ).apply {
        dueAt = DEFAULT_DUE_AT
        updatedAt = DEFAULT_UPDATED_AT
        assignedUser = user
        stops = listOf(
            stop(
                id = "$id-st-1",
                sequence = 1,
                type = "pickup",
                name = "Zentrallager",
                address = "Am Hafen 4",
                status = "done",
            ),
            stop(
                id = "$id-st-2",
                sequence = 2,
                type = "delivery",
                name = customer,
                address = "Industriestraße 12",
                status = "open",
            ),
        )
        checklist = listOf(
            checklist(id = "$id-ci-1", label = "Übergabe dokumentieren", checked = false, mandatory = true),
            checklist(id = "$id-ci-2", label = "Foto im Auftrag ablegen", checked = false, mandatory = false),
        )
    }

    private fun stop(
        id: String,
        sequence: Int,
        type: String,
        name: String,
        address: String,
        status: String,
    ) = StopDto().apply {
        this.id = id
        this.sequence = sequence
        this.type = type
        this.name = name
        this.address = address
        latitude = BASE_LATITUDE + sequence
        longitude = BASE_LONGITUDE + sequence
        this.status = status
        arrivalWindowFrom = DEFAULT_ARRIVAL_FROM
        arrivalWindowTo = DEFAULT_ARRIVAL_TO
    }

    private fun checklist(
        id: String,
        label: String,
        checked: Boolean,
        mandatory: Boolean,
    ) = ChecklistItemDto().apply {
        this.id = id
        this.label = label
        this.checked = checked
        this.mandatory = mandatory
    }

    private companion object {
        const val DEFAULT_DUE_AT = "2026-05-28T10:00:00Z"
        const val DEFAULT_UPDATED_AT = "2026-05-27T14:00:00Z"
        const val DEFAULT_ARRIVAL_FROM = "2026-05-28T08:00:00Z"
        const val DEFAULT_ARRIVAL_TO = "2026-05-28T10:00:00Z"
        const val BASE_LATITUDE = 52.84
        const val BASE_LONGITUDE = 8.04
    }
}
