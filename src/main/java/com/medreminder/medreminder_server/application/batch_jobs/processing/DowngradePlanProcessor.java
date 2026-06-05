package com.medreminder.medreminder_server.application.batch_jobs.processing;

import com.medreminder.medreminder_server.domain.models.subscription.Plan;
import com.medreminder.medreminder_server.domain.models.subscription.SubscriptionPeriodStatus;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class DowngradePlanProcessor implements ItemProcessor<SubscriptionPeriodEntity, SubscriptionEntity> {

    private final SubscriptionMapper subscriptionMapper;

    public DowngradePlanProcessor(SubscriptionMapper subscriptionMapper) {
        this.subscriptionMapper = subscriptionMapper;
    }

    @Override
    public SubscriptionEntity process(SubscriptionPeriodEntity subscriptionPeriodEntity) {
        SubscriptionEntity subscriptionEntity = subscriptionPeriodEntity
                .getSubscription();

        Plan plan = subscriptionMapper.toDomain(subscriptionEntity.getPlan());
        plan.toFreePlan();
        subscriptionEntity.getPlan().syncPlanData(plan);

        subscriptionEntity.getPeriods()
                .stream()
                .filter(period -> period.getId().equals(subscriptionPeriodEntity.getId()))
                .findFirst()
                .ifPresent(period -> {
                    period.updateStatus(SubscriptionPeriodStatus.COMPLETED.toString());
                });

        return subscriptionEntity;
    }
}