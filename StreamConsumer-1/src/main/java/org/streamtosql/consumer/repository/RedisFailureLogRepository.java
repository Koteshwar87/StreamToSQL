package org.streamtosql.consumer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.streamtosql.consumer.model.RedisFailureLog;

@Repository
public interface RedisFailureLogRepository extends JpaRepository<RedisFailureLog, Long> {
    // Extend with query methods as needed
}