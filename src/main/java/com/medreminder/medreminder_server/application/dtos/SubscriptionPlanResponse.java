package com.medreminder.medreminder_server.application.dtos;

import com.medreminder.medreminder_server.domain.models.subscription.PlanType;

public record SubscriptionPlanResponse(String id,
                                       PlanType planType,
                                       int maxMedications,
                                       boolean managedRelation,
                                       boolean refillReminders,
                                       boolean reminderPreference) {
}
