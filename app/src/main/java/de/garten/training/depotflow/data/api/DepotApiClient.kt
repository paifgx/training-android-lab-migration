package de.garten.training.depotflow.data.api

import de.garten.training.depotflow.core.ResultCallback
import de.garten.training.depotflow.data.api.dto.OutboxDto
import de.garten.training.depotflow.data.api.dto.SyncResponseDto
import de.garten.training.depotflow.data.api.dto.WorkOrderDto

interface DepotApiClient {
    fun loadWorkOrders(
        depotId: String,
        changedSince: String?,
        callback: ResultCallback<List<WorkOrderDto>>
    )

    fun loadWorkOrder(serverId: String, callback: ResultCallback<WorkOrderDto>)

    fun completeWorkOrder(
        serverId: String,
        payload: OutboxDto,
        callback: ResultCallback<WorkOrderDto>
    )

    fun pushOutbox(payload: List<OutboxDto>, callback: ResultCallback<SyncResponseDto>)
}
