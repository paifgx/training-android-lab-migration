package de.garten.training.depotflow.data.mapper;

import org.junit.Test;

import de.garten.training.depotflow.data.api.dto.WorkOrderDto;
import de.garten.training.depotflow.data.db.green.WorkOrder;
import de.garten.training.depotflow.domain.SyncStatus;
import de.garten.training.depotflow.domain.WorkOrderStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorkOrderMapperTest {

    private final WorkOrderMapper mapper = new WorkOrderMapper();

    @Test
    public void mapsDtoToCleanEntity() {
        WorkOrderDto dto = new WorkOrderDto("wo-1", "DF-1", "Test", "Depot", "accepted", 5);
        dto.dueAt = "2026-05-27T10:00:00Z";
        dto.updatedAt = "2026-05-27T09:00:00Z";
        dto.assignedUser = "trainer";

        WorkOrder entity = mapper.fromDto(dto);

        assertEquals("wo-1", entity.serverId);
        assertEquals(WorkOrderStatus.ACCEPTED, entity.status);
        assertEquals(SyncStatus.CLEAN, entity.syncStatus);
        assertFalse(entity.isDirty());
    }

    @Test
    public void dirtyLocalEntityWinsAgainstRemote() {
        WorkOrder local = new WorkOrder();
        local.setDirty(true);
        local.updatedAt = "2026-05-27T09:00:00Z";

        WorkOrderDto remote = new WorkOrderDto("wo-1", "DF-1", "Remote", "Depot", "done", 5);
        remote.updatedAt = "2026-05-27T10:00:00Z";

        assertFalse(mapper.shouldOverwriteLocal(local, remote));
    }

    @Test
    public void newerRemoteEntityCanOverwriteCleanLocalEntity() {
        WorkOrder local = new WorkOrder();
        local.setDirty(false);
        local.updatedAt = "2026-05-27T09:00:00Z";

        WorkOrderDto remote = new WorkOrderDto("wo-1", "DF-1", "Remote", "Depot", "done", 5);
        remote.updatedAt = "2026-05-27T10:00:00Z";

        assertTrue(mapper.shouldOverwriteLocal(local, remote));
    }
}
