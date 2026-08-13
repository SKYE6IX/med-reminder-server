package com.medreminder.medreminder_server.subscription;

import com.medreminder.medreminder_server.domain.models.subscription.*;

import java.time.LocalDateTime;

public class SubscriptionServiceStubFactory {

    public static Plan createPlan(String id) {
        return new Plan(
                id,
                PlanType.FREE,
                2,
                false,
                false,
                false
        );
    }

    public static Subscription createSubscription(String id) {
        return new Subscription(
                id,
                "TEST_STORE",
                SubscriptionStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    public static SubscriptionPeriod createSubscriptionPeriod(String id,
                                                              LocalDateTime start,
                                                              LocalDateTime end) {
        return new SubscriptionPeriod(
                id,
                start,
                end,
                SubscriptionPeriodStatus.ACTIVE
        );
    }
}