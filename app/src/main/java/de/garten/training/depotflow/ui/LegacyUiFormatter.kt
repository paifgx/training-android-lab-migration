package de.garten.training.depotflow.ui

import de.garten.training.depotflow.data.db.green.ChecklistItem
import de.garten.training.depotflow.data.db.green.Stop
import de.garten.training.depotflow.data.db.green.WorkOrder

class LegacyUiFormatter {
    fun formatOrderSubtitle(order: WorkOrder): String {
        return order.customerName + " · Priorität " + order.priority + " · fällig " + nullSafe(order.dueAt)
    }

    fun formatOrderStatus(order: WorkOrder): String {
        val sync = if (order.syncStatus == null) "unknown" else order.syncStatus!!.name
        return "Status: " + order.status + " · Sync: " + sync + (if (order.isDirty) " · lokal geändert" else "")
    }

    fun formatStop(stop: Stop): String {
        return stop.sequence.toString() + ". " + stop.type + " · " + stop.name + "\n" +
                nullSafe(stop.address) + "\n" +
                "Fenster: " + nullSafe(stop.arrivalWindowFrom) + " – " + nullSafe(stop.arrivalWindowTo)
    }

    fun formatChecklistItem(item: ChecklistItem): String {
        val marker = if (item.isChecked) "[x] " else "[ ] "
        val mandatory = if (item.isMandatory) " (Pflicht)" else ""
        return marker + item.label + mandatory + optionalNote(item.note)
    }

    private fun optionalNote(note: String?): String {
        if (note == null || note.trim { it <= ' ' }.isEmpty()) {
            return ""
        }
        return "\n    Notiz: " + note
    }

    private fun nullSafe(value: String?): String {
        return if (value == null) "-" else value
    }
}
