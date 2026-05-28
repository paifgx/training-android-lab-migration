package de.garten.training.depotflow.data.api.dto;

import java.util.List;

public class WorkOrderDto {
    public String id;
    public String externalNumber;
    public String title;
    public String customerName;
    public String status;
    public int priority;
    public String dueAt;
    public String updatedAt;
    public String assignedUser;
    public List<StopDto> stops;
    public List<ChecklistItemDto> checklist;

    public WorkOrderDto() {
    }

    public WorkOrderDto(String id, String externalNumber, String title, String customerName, String status, int priority) {
        this.id = id;
        this.externalNumber = externalNumber;
        this.title = title;
        this.customerName = customerName;
        this.status = status;
        this.priority = priority;
    }
}
