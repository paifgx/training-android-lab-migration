package de.garten.training.depotflow.domain

enum class SyncStatus(private val databaseValue: String) {
    CLEAN("clean"),
    DIRTY("dirty"),
    SYNCING("syncing"),
    FAILED("failed");

    fun toDatabaseValue(): String = databaseValue

    companion object {
        @JvmStatic
        fun fromDatabaseValue(value: String?): SyncStatus {
            if (value == null) {
                return CLEAN
            }
            return entries.firstOrNull { it.databaseValue == value } ?: FAILED
        }
    }
}
