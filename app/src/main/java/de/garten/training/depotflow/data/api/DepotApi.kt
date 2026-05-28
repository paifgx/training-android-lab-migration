package de.garten.training.depotflow.data.api

import de.garten.training.depotflow.data.api.dto.OutboxDto
import de.garten.training.depotflow.data.api.dto.SyncResponseDto
import de.garten.training.depotflow.data.api.dto.WorkOrderDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DepotApi {
    @GET("depots/{depotId}/work-orders")
    fun getWorkOrders(
        @Path("depotId") depotId: String,
        @Query("changedSince") changedSince: String?
    ): Call<List<WorkOrderDto>>

    @GET("work-orders/{serverId}")
    fun getWorkOrder(@Path("serverId") serverId: String): Call<WorkOrderDto>

    @POST("work-orders/{serverId}/complete")
    fun completeWorkOrder(
        @Path("serverId") serverId: String,
        @Body completionPayload: OutboxDto
    ): Call<WorkOrderDto>

    @POST("sync/outbox")
    fun pushOutbox(@Body entries: List<OutboxDto>): Call<SyncResponseDto>
}
