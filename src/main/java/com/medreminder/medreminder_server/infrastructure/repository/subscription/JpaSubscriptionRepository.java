package com.medreminder.medreminder_server.infrastructure.repository.subscription;

import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.repository.BaseJpaRepository;

import java.util.Optional;

public interface JpaSubscriptionRepository extends BaseJpaRepository<SubscriptionEntity, String> {

    Optional<SubscriptionEntity > findByUserId(String userId);
}
