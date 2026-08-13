package com.medreminder.medreminder_server.domain.models.subscription;

import java.time.LocalDateTime;

public class SubscriptionPeriod {

    private final String id;
    private Subscription subscription;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final SubscriptionPeriodStatus status;

    public SubscriptionPeriod(String id,
                              LocalDateTime startAt,
                              LocalDateTime endAt,
                              SubscriptionPeriodStatus status) {
        this.id = id;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public SubscriptionPeriodStatus getStatus() {
        return status;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
