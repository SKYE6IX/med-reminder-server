package com.medreminder.medreminder_server.infrastructure.entity.billing;

import com.medreminder.medreminder_server.domain.models.billing.SubscriptionPeriod;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity(name = "SUBSCRIPTION_PERIODS")
public class SubscriptionPeriodEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "payment_status")
    private String paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubscriptionEntity subscription;

    @OneToOne(
            mappedBy = "subscriptionPeriod",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private PaymentEntity payment;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public SubscriptionPeriodEntity() {
    }

    public SubscriptionPeriodEntity(String id,
                                    LocalDateTime startTime,
                                    LocalDateTime endTime,
                                    String paymentStatus,
                                    SubscriptionEntity subscription) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paymentStatus = paymentStatus;
        this.subscription = subscription;
    }

    public String getId() {
        return id;
    }

    public SubscriptionEntity getSubscription() {
        return subscription;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return paymentStatus;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }

    public void syncSubscriptionPeriodData(SubscriptionPeriod  subscriptionPeriod) {
        this.paymentStatus = subscriptionPeriod.getPaymentStatus().toString();
    }
}
