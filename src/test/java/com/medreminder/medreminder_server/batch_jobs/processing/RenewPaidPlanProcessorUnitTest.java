package com.medreminder.medreminder_server.batch_jobs.processing;


import com.medreminder.medreminder_server.application.batch_jobs.processing.RenewPaidPlanProcessor;
import com.medreminder.medreminder_server.application.batch_jobs.processing.RenewPaidPlanResult;
import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.domain.models.subscription.Plan;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.subscription.SubscriptionServiceStubFactory;
import com.medreminder.medreminder_server.user.UserStubData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RenewPaidPlanProcessorUnitTest {

    private final UserMapper userMapper = new UserMapper();
    private final SubscriptionMapper subscriptionMapper = new SubscriptionMapper();

    @Mock
    private PaymentService paymentService;

    private RenewPaidPlanProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new RenewPaidPlanProcessor(paymentService);
    }


    @Test
    void shouldProcessRenewPaidPlanWithSuccessfulPayment() {
        User stubUser = UserStubData
                .createUserWithId("email", "Test User", null);
        Plan plan = SubscriptionServiceStubFactory
                .createPlan(UUID.randomUUID().toString());
        plan.toProPlan();

        UserEntity stubUserEntity = userMapper.toEntity(stubUser);

        stubUserEntity.setPlan(subscriptionMapper.toEntity(plan, stubUserEntity));

        var stubSubscription = SubscriptionServiceStubFactory
                .createSubscription(UUID.randomUUID().toString());

        var stubSubscriptionEntity = subscriptionMapper
                .toEntity(stubSubscription, stubUserEntity);
        stubSubscriptionEntity.updateTimeZone("Europe/Moscow");

        var stubPeriod = SubscriptionServiceStubFactory
                .createSubscriptionPeriod(UUID.randomUUID().toString(),
                        LocalDateTime.now().minusYears(1), LocalDateTime.now().minusDays(1));
        SubscriptionPeriodEntity stubPeriodEntity = subscriptionMapper
                .toEntity(stubPeriod, stubSubscriptionEntity);
        stubSubscriptionEntity.getPeriods().add(stubPeriodEntity);

        when(paymentService.processRenewPayment(any(), any()))
                .thenReturn(SubscriptionServiceStubFactory.createMockSuccessfulPayment());

        RenewPaidPlanResult result = processor.process(stubPeriodEntity);

        assertThat(result).isNotNull();
        assertThat(result.subscriptionEntity().getPeriods().size()).isGreaterThan(1);
        assertThat(result.newBillingEntity()).isNotNull();
        assertThat(result.subscriptionEntity().getBillingRetry()).isFalse();
    }


    @Test
    void processRenewPaidPlanWithUnsuccessfulPayment() {
        User stubUser = UserStubData
                .createUserWithId("email", "Test User", null);
        Plan plan = SubscriptionServiceStubFactory
                .createPlan(UUID.randomUUID().toString());
        plan.toProPlan();

        UserEntity stubUserEntity = userMapper.toEntity(stubUser);

        stubUserEntity.setPlan(subscriptionMapper.toEntity(plan, stubUserEntity));

        var stubSubscription = SubscriptionServiceStubFactory
                .createSubscription(UUID.randomUUID().toString());

        var stubSubscriptionEntity = subscriptionMapper
                .toEntity(stubSubscription, stubUserEntity);
        stubSubscriptionEntity.updateTimeZone("Europe/Moscow");

        var stubPeriod = SubscriptionServiceStubFactory
                .createSubscriptionPeriod(UUID.randomUUID().toString(),
                        LocalDateTime.now().minusYears(1), LocalDateTime.now().minusDays(1));
        SubscriptionPeriodEntity stubPeriodEntity = subscriptionMapper
                .toEntity(stubPeriod, stubSubscriptionEntity);
        stubSubscriptionEntity.getPeriods().add(stubPeriodEntity);

        when(paymentService.processRenewPayment(any(), any()))
                .thenReturn(SubscriptionServiceStubFactory.createMockUnSuccessfulPayment());

        RenewPaidPlanResult result = processor.process(stubPeriodEntity);

        assertThat(result).isNotNull();
        assertThat(result.subscriptionEntity().getBillingRetry()).isTrue();
        assertThat(result.newBillingEntity()).isNull();
        assertThat(result.subscriptionEntity().getNextRetryBillingAt())
                .isEqualTo(LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS));
    }
}