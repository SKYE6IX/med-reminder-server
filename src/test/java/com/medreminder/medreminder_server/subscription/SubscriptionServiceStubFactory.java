package com.medreminder.medreminder_server.subscription;

import com.medreminder.medreminder_server.domain.models.billing.BillingCycle;
import com.medreminder.medreminder_server.domain.models.subscription.*;
import ru.loolzaaa.youkassa.model.Payment;
import ru.loolzaaa.youkassa.pojo.Amount;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubscriptionServiceStubFactory {

    public static Plan createPlan(String id) {
        return new Plan(
                id,
                PlanType.FREE,
                2,
                false,
                false,
                false
        );
    }

    public static Subscription createSubscription(String id) {
        return new Subscription(
                id,
                SubscriptionStatus.ACTIVE,
                null,
                BillingCycle.ANNUAL,
                true
        );
    }

    public static SubscriptionPeriod createSubscriptionPeriod(String id,
                                                              LocalDateTime start,
                                                              LocalDateTime end) {
        return new SubscriptionPeriod(
                id,
                start,
                end,
                SubscriptionPeriodStatus.ACTIVE,
                SubscriptionPeriodPaymentStatus.PAID
        );
    }

    public static Payment createMockSuccessfulPayment() {
        Amount amount = Amount.builder()
                .currency("RUB")
                .value("3050.00")
                .build();

        Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.builder()
                .id(UUID.randomUUID().toString())
                .status(Payment.Status.SUCCEEDED)
                .type("BANK_CARD")
                .build();

        return Payment.builder()
                .amount(amount)
                .status(Payment.Status.SUCCEEDED)
                .paymentMethod(paymentMethod)
                .build();
    }
}