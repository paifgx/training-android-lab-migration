package de.garten.training.depotflow.data.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.garten.training.depotflow.core.ResultCallback;
import de.garten.training.depotflow.data.api.dto.ChecklistItemDto;
import de.garten.training.depotflow.data.api.dto.OutboxDto;
import de.garten.training.depotflow.data.api.dto.StopDto;
import de.garten.training.depotflow.data.api.dto.SyncResponseDto;
import de.garten.training.depotflow.data.api.dto.WorkOrderDto;

public class FakeDepotApiClient implements DepotApiClient {

    @Override
    public void loadWorkOrders(String depotId, String changedSince, ResultCallback<List<WorkOrderDto>> callback) {
        callback.onSuccess(Arrays.asList(
                order("wo-1001", "DF-2026-1001", "Torprüfung und Scanner-Tausch", "Norddepot Cloppenburg", "accepted", 8, "m.schneider"),
                order("wo-1002", "DF-2026-1002", "Ersatzteile an Außenlager liefern", "Servicepunkt Bremen", "in_progress", 5, "l.koenig"),
                order("wo-1003", "DF-2026-1003", "Temperatursensor im Außenlager prüfen", "Kühlbereich Oldenburg", "new", 9, "a.weber")
        ));
    }

    @Override
    public void loadWorkOrder(String serverId, ResultCallback<WorkOrderDto> callback) {
        callback.onSuccess(order(serverId, "DF-2026-DEMO", "Remote-Detail nachladen", "Demo-Depot", "accepted", 3, "demo.user"));
    }

    @Override
    public void completeWorkOrder(String serverId, OutboxDto payload, ResultCallback<WorkOrderDto> callback) {
        WorkOrderDto completed = order(serverId, "DF-2026-COMPLETE", "Lokal erledigter Auftrag", "Demo-Depot", "done", 1, "demo.user");
        completed.updatedAt = "2026-05-27T14:40:00Z";
        callback.onSuccess(completed);
    }

    @Override
    public void pushOutbox(List<OutboxDto> payload, ResultCallback<SyncResponseDto> callback) {
        SyncResponseDto response = new SyncResponseDto();
        response.success = true;
        response.acceptedAggregateIds = new ArrayList<>();
        response.rejectedAggregateIds = new ArrayList<>();
        response.serverTime = "2026-05-27T14:45:00Z";
        if (payload != null) {
            for (OutboxDto entry : payload) {
                response.acceptedAggregateIds.add(entry.aggregateId);
            }
        }
        callback.onSuccess(response);
    }

    private WorkOrderDto order(String id, String externalNumber, String title, String customer, String status, int priority, String user) {
        WorkOrderDto dto = new WorkOrderDto(id, externalNumber, title, customer, status, priority);
        dto.dueAt = "2026-05-28T10:00:00Z";
        dto.updatedAt = "2026-05-27T14:00:00Z";
        dto.assignedUser = user;
        dto.stops = Arrays.asList(
                stop(id + "-st-1", 1, "pickup", "Zentrallager", "Am Hafen 4", "done"),
                stop(id + "-st-2", 2, "delivery", customer, "Industriestraße 12", "open")
        );
        dto.checklist = Arrays.asList(
                checklist(id + "-ci-1", "Übergabe dokumentieren", false, true),
                checklist(id + "-ci-2", "Foto im Auftrag ablegen", false, false)
        );
        return dto;
    }

    private StopDto stop(String id, int sequence, String type, String name, String address, String status) {
        StopDto dto = new StopDto();
        dto.id = id;
        dto.sequence = sequence;
        dto.type = type;
        dto.name = name;
        dto.address = address;
        dto.latitude = 52.84 + sequence;
        dto.longitude = 8.04 + sequence;
        dto.status = status;
        dto.arrivalWindowFrom = "2026-05-28T08:00:00Z";
        dto.arrivalWindowTo = "2026-05-28T10:00:00Z";
        return dto;
    }

    private ChecklistItemDto checklist(String id, String label, boolean checked, boolean mandatory) {
        ChecklistItemDto dto = new ChecklistItemDto();
        dto.id = id;
        dto.label = label;
        dto.checked = checked;
        dto.mandatory = mandatory;
        dto.note = null;
        return dto;
    }
}
