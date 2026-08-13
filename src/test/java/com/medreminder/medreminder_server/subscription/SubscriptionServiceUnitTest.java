package com.medreminder.medreminder_server.subscription;


import com.medreminder.medreminder_server.application.dtos.subscription.PaidSubscriptionRequest;
import com.medreminder.medreminder_server.domain.models.subscription.Plan;
import com.medreminder.medreminder_server.domain.models.subscription.PlanType;
import com.medreminder.medreminder_server.domain.models.subscription.Subscription;
import com.medreminder.medreminder_server.domain.models.users.User;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionRepository;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionService;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionServiceImpl;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import com.medreminder.medreminder_server.user.UserStubData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
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

    private SubscriptionService subscriptionService;

    private final UserMapper userMapper = new UserMapper();
    private final SubscriptionMapper subscriptionMapper = new SubscriptionMapper();

    @BeforeEach
    void setUp() {
        SubscriptionMapper subscriptionMapper = new SubscriptionMapper();
        subscriptionService = new SubscriptionServiceImpl(
                subscriptionRepository, userRepository, subscriptionMapper);
    }

    @Test
    void shouldCreateSubscription() {
        User stubUser = UserStubData
                .createUserWithId("email", "Test User", null);
        Plan plan = SubscriptionServiceStubFactory
                .createPlan(UUID.randomUUID().toString());

        UserEntity stubUserEntity = userMapper.toEntity(stubUser);
        stubUserEntity.setPlan(subscriptionMapper.toEntity(plan,stubUserEntity));

        Subscription stubSubscription = SubscriptionServiceStubFactory
                .createSubscription(UUID.randomUUID().toString());

        SubscriptionEntity stubSubscriptionEntity = subscriptionMapper
                .toEntity(stubSubscription,stubUserEntity);

        PaidSubscriptionRequest request = new PaidSubscriptionRequest(
                LocalDateTime.now().getMinute(),
                LocalDateTime.now().getMinute(),
                LocalDate.now().plusDays(10).atStartOfDay().getMinute(),
                "LOCAL_STORE",
                "Europe/Moscow"
        );

        when(userRepository.findUserById(any(String.class)))
                .thenReturn(Optional.of(stubUserEntity));

        when(subscriptionRepository.saveSubscription(any(SubscriptionEntity.class)))
                .thenReturn(stubSubscriptionEntity);

        Map<String,String> response = subscriptionService
                .createPaidSubscriptionPlan(request, stubUserEntity.getId());

        assertThat(response.get("plan")).isEqualTo(PlanType.PRO.toString());
    }
}

