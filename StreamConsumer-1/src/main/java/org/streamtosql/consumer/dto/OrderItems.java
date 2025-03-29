package org.streamtosql.consumer.dto;

import lombok.Data;

@Data
public class OrderItems extends BaseMessage {
    private String orderId;
    private String productId;
    private int quantity;
    private double price;

    public OrderItems() {
        setDataTypeEnum(DataTypeEnum.DATA);
        setCategoryEnum(CategoryEnum.ORDER_ITEMS);
    }
}