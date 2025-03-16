package org.streamtosql.producer.model;

import lombok.Data;

@Data
public class OrderMetada extends BaseMessage {
    private double speed;
    private double fuelLevel;

    public OrderMetada() {
        setDataTypeEnum(DataTypeEnum.DATA);
        setCategoryEnum(CategoryEnum.ORDER_METADATA);
    }
}
