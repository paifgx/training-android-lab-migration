package de.garten.training.depotflow.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SyncPolicyTest {

    private final SyncPolicy policy = new SyncPolicy();

    @Test
    public void retriesOnlyUpToLimit() {
        assertTrue(policy.shouldRetry(0, null));
        assertTrue(policy.shouldRetry(2, "timeout"));
        assertFalse(policy.shouldRetry(3, "timeout"));
    }

    @Test
    public void doesNotRetryUnauthorizedErrors() {
        assertFalse(policy.shouldRetry(1, "HTTP 401"));
    }

    @Test
    public void terminalStatusChangeCreatesOutboxEntry() {
        assertTrue(policy.createsOutboxEntry(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.DONE));
        assertFalse(policy.createsOutboxEntry(WorkOrderStatus.ACCEPTED, WorkOrderStatus.IN_PROGRESS));
    }
}
