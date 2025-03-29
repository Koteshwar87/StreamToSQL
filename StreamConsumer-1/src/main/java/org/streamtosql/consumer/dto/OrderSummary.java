package org.streamtosql.consumer.model;

import lombok.Data;

@Data
public class OrderSummary extends BaseMessage {
    private String orderId;
    private double totalAmount;
    private String paymentStatus;

    public OrderSummary() {
        setDataTypeEnum(DataTypeEnum.DATA);
        setCategoryEnum(CategoryEnum.ORDER_SUMMARY);
    }
}
