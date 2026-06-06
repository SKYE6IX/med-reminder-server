package com.medreminder.medreminder_server.infrastructure.entity.subscription;


import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "SUBSCRIPTIONS")
public class SubscriptionEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    private String status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "billing_cycle")
    private String billingCycle;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "is_billing_retry")
    private Boolean isBillingRetry;

    @Column(name = "next_retry_billing_at")
    private LocalDateTime nextRetryBillingAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "plan_id")
    private PlanEntity plan;

    @OneToMany(
            mappedBy = "subscription",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("createdAt ASC")
    private final List<SubscriptionPeriodEntity> periods = new ArrayList<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public SubscriptionEntity() {
    }

    public SubscriptionEntity(String id,
                              String status,
                              LocalDateTime startedAt,
                              String billingCycle,
                              Boolean isBillingRetry,
                              UserEntity user,
                              PlanEntity plan) {
        this.id = id;
        this.status = status;
        this.startedAt = startedAt;
        this.billingCycle = billingCycle;
        this.isBillingRetry = isBillingRetry;
        this.user = user;
        this.plan = plan;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public List<SubscriptionPeriodEntity> getPeriods() {
        return periods;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public String getBillingCycle() {
        return billingCycle;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public UserEntity getUser() {
        return user;
    }

    public PlanEntity getPlan() {
        return plan;
    }

    public Boolean getBillingRetry() {
        return isBillingRetry;
    }

    public LocalDateTime getNextRetryBillingAt() {
        return nextRetryBillingAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void updateCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    public void updateBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    public void updateTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void updateIsBillingRetry(Boolean isBillingRetry) {
        this.isBillingRetry = isBillingRetry;
    }

    public void updateNextRetryBillingAt(LocalDateTime nextRetryBillingAt) {
        this.nextRetryBillingAt = nextRetryBillingAt;
    }
}
