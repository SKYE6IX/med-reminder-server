package com.medreminder.medreminder_server.domain.models.subscription;

import com.medreminder.medreminder_server.domain.models.billing.BillingCycle;
import com.medreminder.medreminder_server.domain.models.users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Subscription {

    private final String id;
    private final SubscriptionStatus status;
    private final List<SubscriptionPeriod> periods = new ArrayList<>();
    private final LocalDateTime startedAt;
    private LocalDateTime canceledAt;
    private final BillingCycle billingCycle;
    private final boolean autoRenewal;
    private Plan plan;
    private User user;

    public Subscription(String id,
                        SubscriptionStatus status,
                        LocalDateTime startedAt,
                        BillingCycle billingCycle,
                        boolean autoRenewal) {
        this.id = id;
        this.status = status;
        this.startedAt = startedAt;
        this.billingCycle = billingCycle;
        this.autoRenewal = autoRenewal;
    }

    public String getId() {
        return id;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public List<SubscriptionPeriod> getPeriods() {
        return periods;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public boolean isAutoRenewal() {
        return autoRenewal;
    }

    public Plan getPlan() {
        return plan;
    }

    public User getUser() {
        return user;
    }
}