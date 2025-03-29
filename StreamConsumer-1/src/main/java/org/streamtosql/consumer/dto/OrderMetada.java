package org.streamtosql.consumer.dto;

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
