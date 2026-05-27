package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.application.dtos.SubscriptionPlanResponse;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.domain.models.subscription.PlanType;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;


public class SubscriptionServiceImpl implements SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public SubscriptionPlanResponse createPaidSubscriptionPlan() {
        return null;
    }

//    What do we need to create a paid subscription
//    1 User need to make payment
//    2 We need to confirm user payment
//    3 If payment is sucsseful, we moved on to create a new
//      paid subscription
//    4 If payment failed, we don't create a paid subscription
//    5 Depend on how the payment system will work, we might
//      need to set up an intent entity, and then listen to
//      a callback about payment status before creating the payment

    @Override
    public SubscriptionPlanResponse cancelPaidSubscriptionPlan() {
        return null;
    }

    @Override
    public SubscriptionPlanResponse getSubscriptionPlanByUserId(String userId) {

        PlanEntity planEntity = subscriptionRepository.getPlanByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Plan not found"));

        return new SubscriptionPlanResponse(
                planEntity.getId(),
                PlanType.valueOf(planEntity.getPlanType()),
                planEntity.getMaxMedications(),
                planEntity.isManagedRelation(),
                planEntity.isRefillReminders(),
                planEntity.isReminderPreference());
    }
}
