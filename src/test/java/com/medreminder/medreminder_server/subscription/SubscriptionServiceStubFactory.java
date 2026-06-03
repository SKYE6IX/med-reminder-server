package com.medreminder.medreminder_server.subscription;

import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import ru.loolzaaa.youkassa.model.Payment;
import ru.loolzaaa.youkassa.pojo.Amount;

import java.util.UUID;

public class SubscriptionServiceStubFactory {

    public static UserEntity createStubUserWithPlan() {
        UserEntity userEntity = new UserEntity(
                UUID.randomUUID().toString(),
                "mock@mail.com",
                "mock user",
                null,
                "mock provider"
        );

        PlanEntity planEntity = new PlanEntity(
                UUID.randomUUID().toString(),
                "FREE",
                1,
                false,
                false,
                false,
                userEntity
        );

        userEntity.setPlan(planEntity);
        return userEntity;
    }


    public static SubscriptionEntity createMockSubscriptionEntity() {
        return new SubscriptionEntity(
                UUID.randomUUID().toString(),
                "ACTIVE",
                null,
                "ANNUAL",
                true,
                SubscriptionServiceStubFactory.createStubUserWithPlan(),
                SubscriptionServiceStubFactory.createStubUserWithPlan().getPlan()
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