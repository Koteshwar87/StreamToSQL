package org.streamtosql.consumer.model;

import lombok.Data;

@Data
public abstract class BaseMessage {
    private Long id;
    private DataTypeEnum dataTypeEnum;
    private CategoryEnum categoryEnum;
}
