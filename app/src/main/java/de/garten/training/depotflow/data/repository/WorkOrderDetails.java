package de.garten.training.depotflow.data.repository;

import java.util.List;

import de.garten.training.depotflow.data.db.green.ChecklistItem;
import de.garten.training.depotflow.data.db.green.Stop;
import de.garten.training.depotflow.data.db.green.WorkOrder;

public class WorkOrderDetails {
    private final WorkOrder workOrder;
    private final List<Stop> stops;
    private final List<ChecklistItem> checklistItems;

    public WorkOrderDetails(WorkOrder workOrder, List<Stop> stops, List<ChecklistItem> checklistItems) {
        this.workOrder = workOrder;
        this.stops = stops;
        this.checklistItems = checklistItems;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public List<ChecklistItem> getChecklistItems() {
        return checklistItems;
    }
}
