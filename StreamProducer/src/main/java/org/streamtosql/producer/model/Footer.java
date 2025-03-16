package org.streamtosql.producer.model;

import lombok.Data;

@Data
public class Footer extends BaseMessage {
    private Integer count; // Number of `DATA` messages expected

    public Footer() {
        setDataTypeEnum(DataTypeEnum.FOOTER);
    }
}
