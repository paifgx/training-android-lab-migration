package de.garten.training.depotflow.domain

enum class WorkOrderStatus(private val serverValue: String) {
    NEW("new"),
    ACCEPTED("accepted"),
    IN_PROGRESS("in_progress"),
    BLOCKED("blocked"),
    DONE("done"),
    CANCELLED("cancelled");

    fun toServerValue(): String = serverValue

    fun isTerminal(): Boolean = this == DONE || this == CANCELLED

    companion object {
        @JvmStatic
        fun fromServerValue(value: String?): WorkOrderStatus {
            return entries.firstOrNull { it.serverValue == value } ?: NEW
        }
    }
}
