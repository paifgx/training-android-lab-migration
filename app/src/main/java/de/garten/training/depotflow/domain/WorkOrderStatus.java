package de.garten.training.depotflow.domain;

public enum WorkOrderStatus {
    NEW("new"),
    ACCEPTED("accepted"),
    IN_PROGRESS("in_progress"),
    BLOCKED("blocked"),
    DONE("done"),
    CANCELLED("cancelled");

    private final String serverValue;

    WorkOrderStatus(String serverValue) {
        this.serverValue = serverValue;
    }

    public String toServerValue() {
        return serverValue;
    }

    public static WorkOrderStatus fromServerValue(String value) {
        if (value == null) {
            return NEW;
        }
        for (WorkOrderStatus status : values()) {
            if (status.serverValue.equals(value)) {
                return status;
            }
        }
        return NEW;
    }

    public boolean isTerminal() {
        return this == DONE || this == CANCELLED;
    }
}
