package de.garten.training.depotflow.data.api;

import java.util.List;

import de.garten.training.depotflow.data.api.dto.OutboxDto;
import de.garten.training.depotflow.data.api.dto.SyncResponseDto;
import de.garten.training.depotflow.data.api.dto.WorkOrderDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DepotApi {

    @GET("depots/{depotId}/work-orders")
    Call<List<WorkOrderDto>> getWorkOrders(
            @Path("depotId") String depotId,
            @Query("changedSince") String changedSince
    );

    @GET("work-orders/{serverId}")
    Call<WorkOrderDto> getWorkOrder(@Path("serverId") String serverId);

    @POST("work-orders/{serverId}/complete")
    Call<WorkOrderDto> completeWorkOrder(
            @Path("serverId") String serverId,
            @Body OutboxDto completionPayload
    );

    @POST("sync/outbox")
    Call<SyncResponseDto> pushOutbox(@Body List<OutboxDto> entries);
}
