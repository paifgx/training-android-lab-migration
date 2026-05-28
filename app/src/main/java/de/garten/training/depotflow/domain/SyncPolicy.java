package de.garten.training.depotflow.domain;

public class SyncPolicy {

    private static final int MAX_ATTEMPTS = 3;

    public boolean shouldRetry(int attempts, String lastError) {
        if (attempts >= MAX_ATTEMPTS) {
            return false;
        }
        return lastError == null || !lastError.contains("401");
    }

    public boolean createsOutboxEntry(WorkOrderStatus oldStatus, WorkOrderStatus newStatus) {
        if (oldStatus == null || newStatus == null) {
            return false;
        }
        return oldStatus != newStatus && newStatus.isTerminal();
    }
}
