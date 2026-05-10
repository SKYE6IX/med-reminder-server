package com.medreminder.medreminder_server.domain.models.billing;

import com.medreminder.medreminder_server.domain.models.users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Subscription {

    private final String id;
    private SubscriptionStatus status;
    private final List<SubscriptionPeriod> periods = new ArrayList<>();
    private final LocalDateTime startedAt;
    private LocalDateTime canceledAt;
    private BillingCycle billingCycle;
    private boolean autoRenewal;
    private Plan plan;
    private User user;

    public Subscription(String id,
                        SubscriptionStatus status,
                        LocalDateTime startedAt,
                        LocalDateTime canceledAt,
                        BillingCycle billingCycle,
                        boolean autoRenewal) {
        this.id = id;
        this.status = status;
        this.startedAt = startedAt;
        this.canceledAt = canceledAt;
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

    public void addPeriod(SubscriptionPeriod period) {
        periods.add(period);
        period.setSubscription(this);
    }

    public void updateStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public void updateAutoRenewal(boolean autoRenewal) {
        this.autoRenewal = autoRenewal;
    }

    public void updateBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public void updateCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }
}
