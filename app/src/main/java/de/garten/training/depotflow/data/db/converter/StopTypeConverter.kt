package de.garten.training.depotflow.data.db.converter

import de.garten.training.depotflow.domain.StopType
import org.greenrobot.greendao.converter.PropertyConverter

class StopTypeConverter : PropertyConverter<StopType?, String?> {
    override fun convertToEntityProperty(databaseValue: String?): StopType? {
        return StopType.fromDatabaseValue(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: StopType?): String? {
        if (entityProperty == null) {
            return StopType.SERVICE.toDatabaseValue()
        }
        return entityProperty.toDatabaseValue()
    }
}
