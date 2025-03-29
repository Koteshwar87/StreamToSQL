package org.streamtosql.consumer.dto;

import lombok.Data;

@Data
public class Header extends BaseMessage {
    public Header() {
        setDataTypeEnum(DataTypeEnum.HEADER);
    }
}
