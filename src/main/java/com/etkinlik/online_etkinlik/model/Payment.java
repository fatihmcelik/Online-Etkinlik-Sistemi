package com.etkinlik.online_etkinlik.model;

import com.etkinlik.online_etkinlik.model.enums.PaymentStatus;
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

    @Column(nullable = false)
    private String currency = "TRY";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    // YENİ EKLENEN: Veritabanındaki "Field 'payment_method' doesn't have a default value" hatasını çözer
    @Column(name = "payment_method")
    private String paymentMethod = "Kredi Kartı";

    @Column(unique = true)
    private String iyzicoPaymentId;

    private String iyzicoToken;
    private String fraudStatus;
    private String errorCode;
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

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
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    // YENİ EKLENEN: PaymentMethod Getter ve Setter
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
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