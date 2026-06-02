package com.medreminder.medreminder_server.infrastructure.entity.subscription;


import com.medreminder.medreminder_server.domain.models.subscription.Subscription;
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

    @Column(name = "auto_renewal")
    private boolean autoRenewal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @OneToOne(fetch = FetchType.LAZY)
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
                              boolean autoRenewal,
                              UserEntity user,
                              PlanEntity plan) {
        this.id = id;
        this.status = status;
        this.startedAt = startedAt;
        this.billingCycle = billingCycle;
        this.autoRenewal = autoRenewal;
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

    public boolean isAutoRenewal() {
        return autoRenewal;
    }

    public UserEntity getUser() {
        return user;
    }

    public PlanEntity getPlan() {
        return plan;
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
}
