package de.garten.training.depotflow.data.db.converter;

import org.greenrobot.greendao.converter.PropertyConverter;

import de.garten.training.depotflow.domain.StopType;

public class StopTypeConverter implements PropertyConverter<StopType, String> {

    @Override
    public StopType convertToEntityProperty(String databaseValue) {
        return StopType.fromDatabaseValue(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(StopType entityProperty) {
        if (entityProperty == null) {
            return StopType.SERVICE.toDatabaseValue();
        }
        return entityProperty.toDatabaseValue();
    }
}
