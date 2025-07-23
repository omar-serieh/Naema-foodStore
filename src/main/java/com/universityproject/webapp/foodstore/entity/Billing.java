package com.universityproject.webapp.foodstore.entity;


import com.universityproject.webapp.foodstore.repository.PointsSystemRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "billing")
public class Billing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_id")
    private int billingId;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private paymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private paymentStatus paymentStatus;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cartId;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Users user;
    @Column(name = "items_snapshot",columnDefinition = "TEXT")
    private  String itemsSnapshot;
    @Column(name = "earned_points")
    private Double earnedPoints;



    @PrePersist
    protected void onCreate() {
        this.createdAt = Timestamp.from(Instant.now());
    }
    public enum paymentMethod {
        CREDIT_CARD,
        PAYPAL,
        BANK_TRANSFER,
        BY_POINTS,
        CASH
    }

    public enum paymentStatus {
        PENDING,
        COMPLETED,
        FAILED
    }


    public Billing() {}

    public Billing(double totalAmount, Timestamp createdAt, paymentMethod paymentMethod, paymentStatus paymentStatus) {
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;

    }

    public Double getEarnedPoints() {
        return earnedPoints;
    }

    public void setEarnedPoints(Double earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public String getItemsSnapshot() {
        return itemsSnapshot;
    }

    public void setItemsSnapshot(String itemsSnapshot) {
        this.itemsSnapshot = itemsSnapshot;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Billing.paymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Cart getCartId() {
        return cartId;
    }

    public void setCartId(Cart cartId) {
        this.cartId = cartId;
    }

    public void setPaymentMethod(Billing.paymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Billing.paymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Billing.paymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }



    public int getBillingId() {
        return billingId;
    }

    public void setBillingId(int billingId) {
        this.billingId = billingId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


}
