package org.streamtosql.consumer.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class EntityJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public EntityJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*@Transactional
    public void batchInsert(List<KafkaMessage> entities) {
        String sql = "INSERT INTO my_table (id, data) VALUES (?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET data = EXCLUDED.data";

        *//*jdbcTemplate.batchUpdate(sql, entities, 1000, (ps, entity) -> {
            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getData());
        });*//*
    }*/
}
