package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.application.dtos.subscription.PaidSubscriptionRequest;
import com.medreminder.medreminder_server.application.dtos.subscription.SubscriptionPlanResponse;
import com.medreminder.medreminder_server.domain.services.UseCase;

import java.util.Map;

public interface SubscriptionService extends UseCase {

    SubscriptionPlanResponse createPaidSubscriptionPlan(PaidSubscriptionRequest request, String userId);

    Map<String, String> cancelPaidSubscriptionPlan(String userId);

    SubscriptionPlanResponse getSubscriptionPlanByUserId(String userId);
}