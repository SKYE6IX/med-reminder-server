package com.medreminder.medreminder_server.infrastructure.entity.billing;

import com.medreminder.medreminder_server.domain.models.billing.Payment;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "PAYMENTS")
public class PaymentEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    private BigDecimal amount;

    private String method;

    private String status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_period_id")
    private SubscriptionPeriodEntity subscriptionPeriod;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public PaymentEntity() {
    }

    public PaymentEntity(String id,
                         BigDecimal amount,
                         String method,
                         String status,
                         UserEntity user,
                         SubscriptionPeriodEntity subscriptionPeriod) {
        this.id = id;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.user = user;
        this.subscriptionPeriod = subscriptionPeriod;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public UserEntity getUser() {
        return user;
    }

    public SubscriptionPeriodEntity getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void syncPaymentData(Payment domainPayment){
        this.status = domainPayment.getPaymentStatus().toString();
        this.paidAt = domainPayment.getPaidAt();
    }
}
