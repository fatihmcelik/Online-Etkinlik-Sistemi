package com.etkinlik.online_etkinlik.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_items")
public class PaymentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @Column(nullable = false)
    private String itemId;

   
    @Column(nullable = false)
    private String itemName;

    
    @Column(nullable = false)
    private BigDecimal itemPrice;

    
    @Column(nullable = false)
    private Integer itemQuantity = 1;

   
    private String category1;

    
    private String category2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public BigDecimal getItemPrice() { return itemPrice; }
    public void setItemPrice(BigDecimal itemPrice) { this.itemPrice = itemPrice; }

    public Integer getItemQuantity() { return itemQuantity; }
    public void setItemQuantity(Integer itemQuantity) { this.itemQuantity = itemQuantity; }

    public String getCategory1() { return category1; }
    public void setCategory1(String category1) { this.category1 = category1; }

    public String getCategory2() { return category2; }
    public void setCategory2(String category2) { this.category2 = category2; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
}