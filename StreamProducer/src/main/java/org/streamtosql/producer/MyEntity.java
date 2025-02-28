package org.streamtosql.producer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyEntity {
    String uuid;

    String indexKey;

    Long indexValue;
}
