package com.medreminder.medreminder_server.domain.models.subscription;

import com.medreminder.medreminder_server.domain.models.users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Subscription {

    private final String id;
    private final String store;
    private final SubscriptionStatus status;
    private final List<SubscriptionPeriod> periods = new ArrayList<>();
    private final LocalDateTime startedAt;
    private LocalDateTime canceledAt;
    private Plan plan;
    private User user;

    public Subscription(String id,
                        String store,
                        SubscriptionStatus status,
                        LocalDateTime startedAt) {
        this.id = id;
        this.store = store;
        this.status = status;
        this.startedAt = startedAt;
    }

    public String getId() {
        return id;
    }

    public String getStore() {
        return store;
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

    public Plan getPlan() {
        return plan;
    }

    public User getUser() {
        return user;
    }
}