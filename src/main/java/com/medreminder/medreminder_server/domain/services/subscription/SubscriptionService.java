package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.application.dtos.SubscriptionPlanResponse;
import com.medreminder.medreminder_server.domain.services.UseCase;

public interface SubscriptionService extends UseCase {

    SubscriptionPlanResponse createPaidSubscriptionPlan();

    SubscriptionPlanResponse cancelPaidSubscriptionPlan();

    SubscriptionPlanResponse getSubscriptionPlanByUserId(String userId);
}