package com.medreminder.medreminder_server.application.batch_jobs.processing;

import com.medreminder.medreminder_server.infrastructure.entity.billing.BillingEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;

public record RenewPaidPlanResult(SubscriptionEntity subscriptionEntity, BillingEntity newBillingEntity) {}
