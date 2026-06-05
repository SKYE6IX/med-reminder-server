package com.medreminder.medreminder_server.batch_jobs.processing;


import com.medreminder.medreminder_server.application.batch_jobs.processing.DowngradePlanProcessor;
import com.medreminder.medreminder_server.domain.models.subscription.Plan;
import com.medreminder.medreminder_server.domain.models.subscription.PlanType;
import com.medreminder.medreminder_server.domain.models.subscription.Subscription;
import com.medreminder.medreminder_server.domain.models.subscription.SubscriptionPeriod;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.subscription.SubscriptionServiceStubFactory;
import com.medreminder.medreminder_server.user.UserStubData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class DowngradePlanProcessorUnitTest {

    private final UserMapper userMapper = new UserMapper();
    private final SubscriptionMapper subscriptionMapper = new SubscriptionMapper();

    private DowngradePlanProcessor processor;

    @BeforeEach
    void setUp() {
        final SubscriptionMapper subscriptionMapper = new SubscriptionMapper();
        processor = new DowngradePlanProcessor(subscriptionMapper);
    }

    @Test
    void shouldProcessDowngradePlan() {
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

        SubscriptionEntity result = processor.process(stubPeriodEntity);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(stubSubscriptionEntity.getId());
        assertThat(result.getPlan().getPlanType()).isEqualTo(PlanType.FREE.toString());
    }
}
