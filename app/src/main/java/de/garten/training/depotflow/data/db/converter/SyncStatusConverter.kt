package de.garten.training.depotflow.data.db.converter

import de.garten.training.depotflow.domain.SyncStatus
import org.greenrobot.greendao.converter.PropertyConverter

class SyncStatusConverter : PropertyConverter<SyncStatus?, String?> {
    override fun convertToEntityProperty(databaseValue: String?): SyncStatus? {
        return SyncStatus.fromDatabaseValue(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: SyncStatus?): String? {
        if (entityProperty == null) {
            return SyncStatus.CLEAN.toDatabaseValue()
        }
        return entityProperty.toDatabaseValue()
    }
}
