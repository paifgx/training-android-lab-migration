package de.garten.training.depotflow.domain

enum class StopType(private val databaseValue: String) {
    PICKUP("pickup"),
    DELIVERY("delivery"),
    SERVICE("service"),
    RETURN("return");

    fun toDatabaseValue(): String = databaseValue

    companion object {
        @JvmStatic
        fun fromDatabaseValue(value: String?): StopType {
            return entries.firstOrNull { it.databaseValue == value } ?: SERVICE
        }
    }
}
