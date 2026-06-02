package com.medreminder.medreminder_server.subscription;


import com.medreminder.medreminder_server.application.dtos.subscription.PaidSubscriptionRequest;
import com.medreminder.medreminder_server.application.dtos.subscription.SubscriptionPlanResponse;
import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.domain.models.subscription.PlanType;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionRepository;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionService;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionServiceImpl;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceUnitTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentService paymentService;

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper();
        subscriptionService = new SubscriptionServiceImpl(
                subscriptionRepository,userRepository,paymentService,subscriptionMapper);

        when(paymentService.processPayment(any(),any()))
                .thenReturn(SubscriptionServiceStubFactory.createMockSuccessfulPayment());
    }

    @Test
    void shouldCreateSubscription() {
        UserEntity mockUser = SubscriptionServiceStubFactory
                .createMockUserEntity();

        SubscriptionEntity mockSubscription = SubscriptionServiceStubFactory
                .createMockSubscriptionEntity();

        PaidSubscriptionRequest request = new PaidSubscriptionRequest(
                UUID.randomUUID().toString(),
                "BANK_CARD",
                "3050",
                "ANNUAL",
                "Europe/Moscow"
        );

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(mockUser));

        when(subscriptionRepository.saveSubscription(any(SubscriptionEntity.class)))
                .thenReturn(mockSubscription);

        SubscriptionPlanResponse response = subscriptionService
                .createPaidSubscriptionPlan(request, mockUser.getId());

        assertThat(response).isNotNull();
        assertThat(response.billingCycle()).isEqualTo(request.billingCycle());
        assertThat(response.subscriptionStatus()).isEqualTo("ACTIVE");
        assertThat(response.maxMedications()).isNull();
        assertThat(response.planType()).isEqualTo(PlanType.PRO);
    }
}

