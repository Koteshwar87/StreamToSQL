package org.streamtosql.consumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "dataTypeEnum")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Header.class, name = "HEADER"),
        @JsonSubTypes.Type(value = OrderItems.class, name = "DATA"),
        @JsonSubTypes.Type(value = Footer.class, name = "FOOTER")
}) // This tells Jackson to look for a property named dataTypeEnum to decide which concrete subclass to instantiate.
@Data
public abstract class BaseMessage {
    private Long id;
//    @JsonProperty("dataType")
    private DataTypeEnum dataTypeEnum;
    private CategoryEnum categoryEnum;
}
