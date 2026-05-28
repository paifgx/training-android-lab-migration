package de.garten.training.depotflow.data.db.converter;

import org.greenrobot.greendao.converter.PropertyConverter;

import de.garten.training.depotflow.domain.WorkOrderStatus;

public class WorkOrderStatusConverter implements PropertyConverter<WorkOrderStatus, String> {

    @Override
    public WorkOrderStatus convertToEntityProperty(String databaseValue) {
        return WorkOrderStatus.fromServerValue(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(WorkOrderStatus entityProperty) {
        if (entityProperty == null) {
            return WorkOrderStatus.NEW.toServerValue();
        }
        return entityProperty.toServerValue();
    }
}
