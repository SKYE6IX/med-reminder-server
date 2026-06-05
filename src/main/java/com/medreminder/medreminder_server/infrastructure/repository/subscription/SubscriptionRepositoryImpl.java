package com.medreminder.medreminder_server.infrastructure.repository.subscription;

import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionRepository;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final JpaSubscriptionRepository jpaSubscriptionRepository;
    private final JpaPlanRepository jpaPlanRepository;

    public SubscriptionRepositoryImpl(JpaSubscriptionRepository jpaSubscriptionRepository,
                                      JpaPlanRepository jpaPlanRepository) {
        this.jpaSubscriptionRepository = jpaSubscriptionRepository;
        this.jpaPlanRepository = jpaPlanRepository;
    }

    @Override
    public SubscriptionEntity saveSubscription(SubscriptionEntity subscriptionEntity) {
        return jpaSubscriptionRepository.save(subscriptionEntity);
    }

    @Override
    public Optional<SubscriptionEntity> getSubscriptionByUserId(String userId) {
        return jpaSubscriptionRepository.findByUserId(userId);
    }

    @Override
    public Optional<PlanEntity> getPlanByUserId(String userId) {
        return jpaPlanRepository.findByUserId(userId);
    }

    @Override
    public void savePlan(PlanEntity planEntity) {
        jpaPlanRepository.save(planEntity);
    }
}
