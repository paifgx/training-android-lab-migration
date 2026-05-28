package de.garten.training.depotflow.domain;

public enum SyncStatus {
    CLEAN("clean"),
    DIRTY("dirty"),
    SYNCING("syncing"),
    FAILED("failed");

    private final String databaseValue;

    SyncStatus(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String toDatabaseValue() {
        return databaseValue;
    }

    public static SyncStatus fromDatabaseValue(String value) {
        if (value == null) {
            return CLEAN;
        }
        for (SyncStatus status : values()) {
            if (status.databaseValue.equals(value)) {
                return status;
            }
        }
        return FAILED;
    }
}
