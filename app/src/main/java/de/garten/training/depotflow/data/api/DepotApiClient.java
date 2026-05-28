package de.garten.training.depotflow.data.api;

import java.util.List;

import de.garten.training.depotflow.core.ResultCallback;
import de.garten.training.depotflow.data.api.dto.OutboxDto;
import de.garten.training.depotflow.data.api.dto.SyncResponseDto;
import de.garten.training.depotflow.data.api.dto.WorkOrderDto;

public interface DepotApiClient {

    void loadWorkOrders(String depotId, String changedSince, ResultCallback<List<WorkOrderDto>> callback);

    void loadWorkOrder(String serverId, ResultCallback<WorkOrderDto> callback);

    void completeWorkOrder(String serverId, OutboxDto payload, ResultCallback<WorkOrderDto> callback);

    void pushOutbox(List<OutboxDto> payload, ResultCallback<SyncResponseDto> callback);
}
