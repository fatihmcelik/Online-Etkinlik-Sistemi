package com.etkinlik.online_etkinlik.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Yapılan işlem: EVENT_CREATED, USER_LOGIN, TICKET_PURCHASED vb.
    @Column(nullable = false)
    private String action;

    // İşlem detayı
    @Column(columnDefinition = "TEXT")
    private String details;

    // YENİ: Hangi tablo etkilendi — EVENT, TICKET, USER vb.
    private String entityType;

    // YENİ: Etkilenen kaydın ID'si
    private Long entityId;

    // YENİ: Kullanıcının IP adresi — brute force tespiti için
    private String ipAddress;

    // YENİ: Tarayıcı bilgisi — güvenlik analizi için
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    // YENİ: String username yerine gerçek FK ilişkisi
    // nullable=true çünkü login başarısız olduğunda user olmayabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}