package de.garten.training.depotflow.data.db.converter;

import org.greenrobot.greendao.converter.PropertyConverter;

import de.garten.training.depotflow.domain.SyncStatus;

public class SyncStatusConverter implements PropertyConverter<SyncStatus, String> {

    @Override
    public SyncStatus convertToEntityProperty(String databaseValue) {
        return SyncStatus.fromDatabaseValue(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(SyncStatus entityProperty) {
        if (entityProperty == null) {
            return SyncStatus.CLEAN.toDatabaseValue();
        }
        return entityProperty.toDatabaseValue();
    }
}
