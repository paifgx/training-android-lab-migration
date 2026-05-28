package de.garten.training.depotflow.ui;

import de.garten.training.depotflow.data.db.green.ChecklistItem;
import de.garten.training.depotflow.data.db.green.Stop;
import de.garten.training.depotflow.data.db.green.WorkOrder;

public class LegacyUiFormatter {

    public String formatOrderSubtitle(WorkOrder order) {
        return order.getCustomerName() + " · Priorität " + order.getPriority() + " · fällig " + nullSafe(order.getDueAt());
    }

    public String formatOrderStatus(WorkOrder order) {
        String sync = order.getSyncStatus() == null ? "unknown" : order.getSyncStatus().name();
        return "Status: " + order.getStatus() + " · Sync: " + sync + (order.isDirty() ? " · lokal geändert" : "");
    }

    public String formatStop(Stop stop) {
        return stop.getSequence() + ". " + stop.getType() + " · " + stop.getName() + "\n" +
                nullSafe(stop.getAddress()) + "\n" +
                "Fenster: " + nullSafe(stop.getArrivalWindowFrom()) + " – " + nullSafe(stop.getArrivalWindowTo());
    }

    public String formatChecklistItem(ChecklistItem item) {
        String marker = item.isChecked() ? "[x] " : "[ ] ";
        String mandatory = item.isMandatory() ? " (Pflicht)" : "";
        return marker + item.getLabel() + mandatory + optionalNote(item.getNote());
    }

    private String optionalNote(String note) {
        if (note == null || note.trim().isEmpty()) {
            return "";
        }
        return "\n    Notiz: " + note;
    }

    private String nullSafe(String value) {
        return value == null ? "-" : value;
    }
}
