package de.garten.training.depotflow.data.db.converter

import de.garten.training.depotflow.domain.WorkOrderStatus
import org.greenrobot.greendao.converter.PropertyConverter

class WorkOrderStatusConverter : PropertyConverter<WorkOrderStatus?, String?> {
    override fun convertToEntityProperty(databaseValue: String?): WorkOrderStatus? {
        return WorkOrderStatus.fromServerValue(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: WorkOrderStatus?): String? {
        if (entityProperty == null) {
            return WorkOrderStatus.NEW.toServerValue()
        }
        return entityProperty.toServerValue()
    }
}
