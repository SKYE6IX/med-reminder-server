package com.medreminder.medreminder_server.domain.models.subscription;

import java.time.LocalDateTime;

public class SubscriptionPeriod {

    private final String id;
    private Subscription subscription;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private SubscriptionPeriodPaymentStatus paymentStatus;

    public SubscriptionPeriod(String id,
                              LocalDateTime startTime,
                              LocalDateTime endTime,
                              SubscriptionPeriodPaymentStatus paymentStatus) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paymentStatus = paymentStatus;
    }

    public String getId() {
        return id;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SubscriptionPeriodPaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public void updatePaymentStatus(SubscriptionPeriodPaymentStatus status) {
        this.paymentStatus = status;
    }
}
