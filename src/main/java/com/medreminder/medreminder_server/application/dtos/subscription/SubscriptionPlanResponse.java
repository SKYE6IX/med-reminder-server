package com.medreminder.medreminder_server.application.dtos.subscription;

import com.medreminder.medreminder_server.domain.models.subscription.PlanType;

public record SubscriptionPlanResponse(String id,
                                       PlanType planType,
                                       Integer maxMedications,
                                       boolean managedRelation,
                                       boolean refillReminders,
                                       boolean reminderPreference,
                                       String endAt,
                                       String billingCycle,
                                       String subscriptionStatus) {
}
