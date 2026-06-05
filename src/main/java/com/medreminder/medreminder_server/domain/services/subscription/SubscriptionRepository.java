package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;

import java.util.Optional;

public interface SubscriptionRepository {

    SubscriptionEntity saveSubscription(SubscriptionEntity subscriptionEntity);

    Optional<SubscriptionEntity> getSubscriptionByUserId(String userId);

    Optional<PlanEntity> getPlanByUserId(String userId);

    void savePlan(PlanEntity planEntity);
}
