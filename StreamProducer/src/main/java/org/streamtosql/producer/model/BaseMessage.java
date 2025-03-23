package org.streamtosql.producer.model;

import lombok.Data;

@Data
public abstract class BaseMessage {
    private Long id;
    private DataTypeEnum dataTypeEnum;
    private CategoryEnum categoryEnum;

    private String correlationId; // 🔄 Used to logically group messages
}
