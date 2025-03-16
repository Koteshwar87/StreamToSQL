package org.streamtosql.consumer.model;

import lombok.Data;

@Data
public class Footer extends BaseMessage {
    private Integer count; // Number of `DATA` messages expected

    public Footer() {
        setDataTypeEnum(DataTypeEnum.FOOTER);
    }
}
