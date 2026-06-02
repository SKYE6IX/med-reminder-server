package com.medreminder.medreminder_server.infrastructure.entity.subscription;

import com.medreminder.medreminder_server.domain.models.subscription.SubscriptionPeriod;
import com.medreminder.medreminder_server.infrastructure.entity.billing.BillingEntity;
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

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubscriptionEntity subscription;

    @OneToOne(
            mappedBy = "subscriptionPeriod",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private BillingEntity billing;

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
                                    String status,
                                    SubscriptionEntity subscription) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paymentStatus = paymentStatus;
        this.status = status;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getStatus() {
        return status;
    }

    public BillingEntity getPayment() {
        return billing;
    }

    public void setPayment(BillingEntity payment) {
        this.billing = payment;
    }

    public void updatePaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
