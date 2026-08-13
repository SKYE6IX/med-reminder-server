package com.medreminder.medreminder_server.infrastructure.entity.subscription;

import jakarta.persistence.*;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity(name = "SUBSCRIPTION_PERIODS")
public class SubscriptionPeriodEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SubscriptionEntity subscription;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public SubscriptionPeriodEntity() {
    }

    public SubscriptionPeriodEntity(String id,
                                    LocalDateTime startAt,
                                    LocalDateTime endAt,
                                    String status,
                                    SubscriptionEntity subscription) {
        this.id = id;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
        this.subscription = subscription;
    }

    public String getId() {
        return id;
    }

    public SubscriptionEntity getSubscription() {
        return subscription;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public String getStatus() {
        return status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
