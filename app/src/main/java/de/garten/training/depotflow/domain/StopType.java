package de.garten.training.depotflow.domain;

public enum StopType {
    PICKUP("pickup"),
    DELIVERY("delivery"),
    SERVICE("service"),
    RETURN("return");

    private final String databaseValue;

    StopType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String toDatabaseValue() {
        return databaseValue;
    }

    public static StopType fromDatabaseValue(String value) {
        if (value == null) {
            return SERVICE;
        }
        for (StopType kind : values()) {
            if (kind.databaseValue.equals(value)) {
                return kind;
            }
        }
        return SERVICE;
    }
}
