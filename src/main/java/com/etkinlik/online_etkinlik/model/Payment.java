package com.etkinlik.online_etkinlik.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    // Para birimi — İyzico TRY zorunlu kılıyor
    @Column(nullable = false)
    private String currency = "TRY";

    // BEKLIYOR, BASARILI, BASARISIZ, IADE
    @Column(nullable = false)
    private String status = "BEKLIYOR";

    // YENİ: İyzico'nun döndürdüğü ödeme ID — sorgu ve iade için zorunlu
    @Column(unique = true)
    private String iyzicoPaymentId;

    // YENİ: 3D Secure akışında kullanıcı geri döndüğünde eşleştirme için
    private String iyzicoToken;

    // YENİ: İyzico'nun dolandırıcılık skoru — güvenlik önlemi
    private String fraudStatus;

    // YENİ: Başarısız ödemelerde hata kodu
    private String errorCode;

    // YENİ: Kullanıcıya gösterilecek hata mesajı
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    // OneToOne yerine ManyToOne — ileride grup bilet alımı için
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIyzicoPaymentId() { return iyzicoPaymentId; }
    public void setIyzicoPaymentId(String iyzicoPaymentId) { this.iyzicoPaymentId = iyzicoPaymentId; }

    public String getIyzicoToken() { return iyzicoToken; }
    public void setIyzicoToken(String iyzicoToken) { this.iyzicoToken = iyzicoToken; }

    public String getFraudStatus() { return fraudStatus; }
    public void setFraudStatus(String fraudStatus) { this.fraudStatus = fraudStatus; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}