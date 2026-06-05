package com.medreminder.medreminder_server.infrastructure.entity.subscription;

import com.medreminder.medreminder_server.domain.models.billing.BillingCycle;
import com.medreminder.medreminder_server.domain.models.subscription.*;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public PlanEntity toEntity(Plan plan, UserEntity user) {
        if (plan == null) {
            return null;
        }
        return new PlanEntity(
                plan.getId(),
                plan.getPlanType().toString(),
                plan.getMaxMedications(),
                plan.isManagedRelation(),
                plan.isRefillReminders(),
                plan.isReminderPreference(),
                user
        );
    }

    public SubscriptionEntity toEntity(Subscription subscription, UserEntity user) {
        if (subscription == null) {
            return null;
        }
        return new SubscriptionEntity(
                subscription.getId(),
                subscription.getStatus().toString(),
                subscription.getStartedAt(),
                subscription.getBillingCycle().toString(),
                subscription.isAutoRenewal(),
                user,
                user.getPlan()
        );
    }

    public SubscriptionPeriodEntity toEntity(SubscriptionPeriod subscriptionPeriod,
                                             SubscriptionEntity subscriptionEntity) {
        if (subscriptionPeriod == null) {
            return null;
        }
        return new SubscriptionPeriodEntity(
                subscriptionPeriod.getId(),
                subscriptionPeriod.getStartTime(),
                subscriptionPeriod.getEndTime(),
                subscriptionPeriod.getStatus().toString(),
                subscriptionPeriod.getPaymentStatus().toString(),
                subscriptionEntity
        );
    }

    public Plan toDomain(PlanEntity planEntity) {
        if (planEntity == null) {
            return null;
        }
        return new Plan(
                planEntity.getId(),
                PlanType.valueOf(planEntity.getPlanType()),
                planEntity.getMaxMedications(),
                planEntity.isManagedRelation(),
                planEntity.isRefillReminders(),
                planEntity.isReminderPreference()
        );
    }
}
