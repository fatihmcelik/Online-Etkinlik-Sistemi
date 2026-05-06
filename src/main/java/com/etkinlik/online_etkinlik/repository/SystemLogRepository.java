package com.etkinlik.online_etkinlik.repository;

import com.etkinlik.online_etkinlik.model.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    List<SystemLog> findByUserId(Long userId);
    List<SystemLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
}