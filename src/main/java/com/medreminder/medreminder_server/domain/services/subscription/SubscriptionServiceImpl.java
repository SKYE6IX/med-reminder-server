package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.application.dtos.subscription.PaidSubscriptionRequest;
import com.medreminder.medreminder_server.application.dtos.subscription.SyncSubscriptionRequest;
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
                subscription,request.latestPurchaseDate(), request.expirationDate(), request.zoneId()
        );

//     Update the subscription plan
        Plan plan = subscriptionMapper.toDomain(subscription.getPlan());
        plan.toProPlan();
        subscription.getPlan().syncPlanEntity(plan);

        if(subscription.getStatus().equals(SubscriptionStatus.CANCELED.toString())){
            subscription.updateStatus(SubscriptionStatus.ACTIVE.toString());
        }

        subscription.getPeriods().add(periodEntity);
        subscriptionRepository
                .saveSubscription(subscription);

        return getPlan(plan.getPlanType().toString());
    }

    @Override
    public Map<String, String> syncSubscriptionWithStore(SyncSubscriptionRequest request, String userId) {
        UserEntity userEntity = userRepository.findUserById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));

        SubscriptionEntity subscriptionEntity = subscriptionRepository
                .getSubscriptionByUserId(userEntity.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Subscription not found!"));

        ZoneId zoneId = ZoneId.of(request.zoneId());

//        IF WILL_RENEW IS FALSE, USER HAS CANCELLED THEIR
//        SUBSCRIPTION, SO WE MOVE FORWARD WITH CANCEL IT HERE TOO.
        if(!request.willRenew()){
            subscriptionEntity.updateStatus(SubscriptionStatus.CANCELED.toString());
            subscriptionEntity.updateStartedAt(null);
            subscriptionEntity.updateCanceledAt(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(request.unsubscribeDetectedAt()),
                    zoneId
            ));
        }

        LocalDateTime now = LocalDateTime.now(zoneId);
        LocalDateTime expirationDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(request.expirationDate()),
                zoneId
        );
        SubscriptionPeriodEntity latestPeriod = subscriptionEntity.getPeriods().getLast();

//        IF EXPIRATION DATE IS BEFORE THE CURRENT DATE,
//        MEANING THE CURRENT PERIOD OF THEIR SUBSCRIPTION HAS EXPIRED,
//        WE DO THE SAME FOR THEIR DATA HERE.
        if(expirationDate.isBefore(now)) {
            Plan plan = subscriptionMapper.toDomain(subscriptionEntity.getPlan());
            plan.toFreePlan();
            subscriptionEntity.getPlan().syncPlanEntity(plan);
            latestPeriod.updateStatus(SubscriptionStatus.CANCELED.toString());
        }

//        IF THE EXPIRATION DATE REQUEST HAS BEEN SHIFT FORWARD
//        MEANING, USER SUBSCRIPTION HAS BEEN RENEW, WE CRREATE
//        A NEW PERIOD FOR USER.
        if(expirationDate.isAfter(latestPeriod.getEndAt())){
            SubscriptionPeriodEntity newPeriod = SubscriptionServiceHelper.createSubscriptionPeriod(
                    subscriptionEntity, request.latestPurchaseDate(), request.expirationDate(), request.zoneId()
            );
            subscriptionEntity.getPeriods().add(newPeriod);
        }


        subscriptionRepository.saveSubscription(subscriptionEntity);
        HashMap<String, String> response = new HashMap<>();
        response.put("status", "success");
        return response;
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