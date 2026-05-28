package de.garten.training.depotflow.domain

class SyncPolicy {
    fun shouldRetry(attempts: Int, lastError: String?): Boolean {
        if (attempts >= MAX_ATTEMPTS) {
            return false
        }
        return lastError == null || !lastError.contains("401")
    }

    fun createsOutboxEntry(oldStatus: WorkOrderStatus?, newStatus: WorkOrderStatus?): Boolean {
        if (oldStatus == null || newStatus == null) {
            return false
        }
        return oldStatus != newStatus && newStatus.isTerminal()
    }

    companion object {
        private const val MAX_ATTEMPTS = 3
    }
}
