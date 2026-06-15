package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.application.dtos.subscription.SubscriptionPlanResponse;
import com.medreminder.medreminder_server.domain.models.billing.BillingCycle;
import com.medreminder.medreminder_server.domain.models.subscription.*;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SubscriptionServiceHelper {

    private final static SubscriptionMapper subscriptionMapper = new SubscriptionMapper();

    public static SubscriptionPeriodEntity createSubscriptionPeriod(
            SubscriptionEntity subscriptionEntity,
            String billingCycle,
            String zoneId) {
        boolean isMonthly = billingCycle.equals(BillingCycle.MONTHLY.toString());
        boolean isAnnual = billingCycle.equals(BillingCycle.ANNUAL.toString());

        LocalDateTime startTime = LocalDateTime.now(ZoneId.of(zoneId));
        LocalDateTime endTime = isMonthly ? startTime.plusDays(30): isAnnual ? startTime.plusYears(1): startTime;

        SubscriptionPeriod newPeriod = new SubscriptionPeriod(
                null,
                startTime,
                endTime,
                SubscriptionPeriodStatus.ACTIVE,
                SubscriptionPeriodPaymentStatus.PENDING
        );
        return subscriptionMapper.toEntity(newPeriod, subscriptionEntity);
    }

    public static SubscriptionPlanResponse getSubscriptionPlanResponse(PlanEntity planEntity,
                                                                 String endAt,
                                                                 String billingCycle,
                                                                 String subscriptionStatus) {
        return new SubscriptionPlanResponse(
                planEntity.getId(),
                PlanType.valueOf(planEntity.getPlanType()),
                planEntity.getMaxMedications(),
                planEntity.isManagedRelation(),
                planEntity.isRefillReminders(),
                planEntity.isReminderPreference(),
                endAt,
                billingCycle,
                subscriptionStatus);
    }

    public static String getBillingCycleAmount(BillingCycle billingCycle) {
        final String monthlyCost = "299";
        final String annualCost = "3588";

        if (billingCycle.equals(BillingCycle.ANNUAL)){
            BigDecimal amount = new BigDecimal(annualCost);
            BigDecimal paddedAmount = amount.setScale(2, RoundingMode.HALF_UP);

            BigDecimal discount = paddedAmount
                    .multiply(new BigDecimal("0.15"))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal finalAmount = paddedAmount.subtract(discount);

            return finalAmount.toString();

        } else if(billingCycle.equals(BillingCycle.MONTHLY)) {
            BigDecimal amount = new BigDecimal(monthlyCost);
            BigDecimal paddedAmount = amount.setScale(2, RoundingMode.HALF_UP);
            return paddedAmount.toString();
        }

        return null;
    }
}
