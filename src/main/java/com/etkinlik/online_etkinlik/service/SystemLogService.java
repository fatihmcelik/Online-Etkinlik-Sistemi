package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.SystemLog;
import com.etkinlik.online_etkinlik.model.User;
import com.etkinlik.online_etkinlik.repository.SystemLogRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class SystemLogService {
    private final SystemLogRepository logRepository;

    public SystemLogService(SystemLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void log(String action, String details, String entityType, Long entityId, User user) {
        SystemLog log = new SystemLog();
        log.setAction(action);
        log.setDetails(details);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setUser(user);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }
}