package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.application.dtos.subscription.PaidSubscriptionRequest;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.domain.models.subscription.*;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   UserRepository userRepository,
                                   SubscriptionMapper subscriptionMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.subscriptionMapper = subscriptionMapper;
    }

    @Override
    public Map<String, String> createPaidSubscriptionPlan(PaidSubscriptionRequest request,
                                                               String userId) {

        UserEntity userEntity = userRepository.findUserById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));

        ZoneId zoneId = ZoneId.of(request.zoneId());

        LocalDateTime subscriptionStartedAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(request.originalPurchaseDate()),
                zoneId
        );

//        Get or Create a subscription
        SubscriptionEntity subscription = getOrCreateSubscriptionEntity(userEntity,
                subscriptionStartedAt, request.store());

//      Create a period for the subscription
        SubscriptionPeriodEntity periodEntity = SubscriptionServiceHelper.createSubscriptionPeriod(
                subscription,request.latestPurchaseDate(),request.expirationDate(),request.zoneId()
        );

//     Update the subscription plan
        Plan plan = subscriptionMapper.toDomain(subscription.getPlan());
        plan.toProPlan();
        subscription.getPlan().syncPlanData(plan);

        if(subscription.getStatus().equals(SubscriptionStatus.CANCELED.toString())){
            subscription.updateStatus(SubscriptionStatus.ACTIVE.toString());
        }

        subscription.getPeriods().add(periodEntity);
        subscriptionRepository
                .saveSubscription(subscription);

        return getPlan(plan.getPlanType().toString());
    }

    @Override
    public Map<String, String> syncSubscriptionWithStore(String userId) {
//        UserEntity userEntity = userRepository.findUserById(userId)
//                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));
//
//        SubscriptionEntity subscriptionEntity = subscriptionRepository
//                .getSubscriptionByUserId(userEntity.getId())
//                .orElseThrow(()-> new ResourceNotFoundException("Subscription not found!"));
//
//        subscriptionEntity.updateStatus(SubscriptionStatus.CANCELED.toString());
//        subscriptionEntity.updateStartedAt(null);
//        subscriptionEntity.updateCanceledAt(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
//
//        userEntity.updatePaymentMethodId(null);
//
//        subscriptionRepository.saveSubscription(subscriptionEntity);
//        userRepository.saveUser(userEntity);
//
//        Map<String, String> result = new HashMap<>();
//        result.put("status", "success");
//
//        return result;
        return  null;
    }

    @Override
    public Map<String, String> getSubscriptionPlanByUserId(String userId) {

        PlanEntity planEntity = subscriptionRepository.getPlanByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Plan not found!"));

        return getPlan(planEntity.getPlanType());
    }

//    HELPER METHODS
    private SubscriptionEntity getOrCreateSubscriptionEntity(UserEntity userEntity,
                                                             LocalDateTime startedAt,
                                                             String store) {
       return subscriptionRepository
                .getSubscriptionByUserId(userEntity.getId())
                .orElseGet(()-> {
                    Subscription subscription = new Subscription(
                            null,
                            store,
                            SubscriptionStatus.ACTIVE,
                            startedAt
                    );
                    return subscriptionRepository
                    .saveSubscription(subscriptionMapper.toEntity(subscription, userEntity));
                });
    }

    private Map<String, String> getPlan(String plan){
        HashMap<String,String> map = new HashMap<>();
        map.put("plan", plan);
        return map;
    }
}