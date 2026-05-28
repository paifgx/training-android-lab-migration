package de.garten.training.depotflow.data.repository

import de.garten.training.depotflow.data.db.green.ChecklistItem
import de.garten.training.depotflow.data.db.green.Stop
import de.garten.training.depotflow.data.db.green.WorkOrder

data class WorkOrderDetails(
    val workOrder: WorkOrder,
    val stops: List<Stop>,
    val checklistItems: List<ChecklistItem>
)
