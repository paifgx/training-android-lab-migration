package de.garten.training.depotflow.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorkOrderStatusTest {

    @Test
    public void parsesStableServerValues() {
        assertEquals(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.fromServerValue("in_progress"));
        assertEquals("done", WorkOrderStatus.DONE.toServerValue());
    }

    @Test
    public void unknownValuesFallBackToNew() {
        assertEquals(WorkOrderStatus.NEW, WorkOrderStatus.fromServerValue("archived_by_old_backend"));
    }

    @Test
    public void terminalStatusesAreKnown() {
        assertTrue(WorkOrderStatus.DONE.isTerminal());
        assertTrue(WorkOrderStatus.CANCELLED.isTerminal());
    }
}
