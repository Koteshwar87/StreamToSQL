package org.streamtosql.producer.model;

import lombok.Data;

@Data
public class Header extends BaseMessage {
    public Header() {
        setDataTypeEnum(DataTypeEnum.HEADER);
    }
}
