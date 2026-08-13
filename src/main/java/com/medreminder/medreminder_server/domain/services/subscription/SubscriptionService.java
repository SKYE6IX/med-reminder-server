package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.application.dtos.subscription.PaidSubscriptionRequest;

import java.util.Map;

public interface SubscriptionService {

    Map<String, String> createPaidSubscriptionPlan(PaidSubscriptionRequest request, String userId);

    Map<String, String> syncSubscriptionWithStore(String userId);

    Map<String, String> getSubscriptionPlanByUserId(String userId);
}