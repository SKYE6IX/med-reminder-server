package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.application.dtos.subscription.PaidSubscriptionRequest;
import com.medreminder.medreminder_server.application.dtos.subscription.SubscriptionPlanResponse;
import com.medreminder.medreminder_server.application.exceptions.PaymentFailedException;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.domain.models.billing.BillingCycle;
import com.medreminder.medreminder_server.domain.models.billing.BillingStatus;
import com.medreminder.medreminder_server.domain.models.subscription.*;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.infrastructure.entity.billing.BillingEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.loolzaaa.youkassa.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   UserRepository userRepository,
                                   PaymentService paymentService,
                                   SubscriptionMapper subscriptionMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.subscriptionMapper = subscriptionMapper;
    }

    @Override
    public SubscriptionPlanResponse createPaidSubscriptionPlan(PaidSubscriptionRequest request,
                                                               String userId) {
        UserEntity userEntity = userRepository.findUserById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));

//        Get or Create a subscription
        SubscriptionEntity subscription = getOrCreateSubscriptionEntity(userEntity,
                request.billingCycle());

//      Create a period for the subscription
        SubscriptionPeriodEntity periodEntity = SubscriptionServiceHelper.createSubscriptionPeriod(
                subscription,request.billingCycle(), request.zoneId()
        );

//        Start a payment process
//        If payment method is BANK_CARD, we can process all in one go.
//        At the moment we only accept bank card only.
        if(request.paymentMethod().equals("BANK_CARD")) {
            final String amount = SubscriptionServiceHelper
                    .getBillingCycleAmount(BillingCycle.valueOf(request.billingCycle()));

            if(amount == null) {
                throw new PaymentFailedException("Payment method not supported!");
            }

            Payment proccessPayment = paymentService.processNewPayment(request.paymentToken(),
                    amount);

            if(proccessPayment == null || proccessPayment.getStatus().equals(Payment.Status.CANCELED)){
                throw new PaymentFailedException("Payment failed!");

            } else if (proccessPayment.getStatus().equals(Payment.Status.SUCCEEDED)) {

//            Update the subscription plan
                Plan plan = subscriptionMapper.toDomain(subscription.getPlan());
                plan.toProPlan();
                subscription.getPlan().syncPlanData(plan);

                periodEntity.updatePaymentStatus(SubscriptionPeriodPaymentStatus.PAID.toString());
                subscription.updateStartedAt(LocalDateTime.now(ZoneId.of(request.zoneId())));

                if(subscription.getStatus().equals(SubscriptionStatus.CANCELED.toString())){
                    subscription.updateStatus(SubscriptionStatus.ACTIVE.toString());
                }
                if(!subscription.getBillingCycle().equals(request.billingCycle())){
                    subscription.updateBillingCycle(request.billingCycle());
                }

//            Add the subscription period to the period list
                subscription.getPeriods().add(periodEntity);
               SubscriptionPeriodEntity savedPeriodEntity = subscriptionRepository
                       .saveSubscription(subscription).getPeriods().getLast();

//            Update user, safe the paymentMethodId return from
//            the processed payment.
                userEntity.updatePaymentMethodId(proccessPayment.getPaymentMethod().getId());
//          Create a new billing for user.
                BillingEntity newBilling = new BillingEntity(
                        null,
                        new BigDecimal(amount),
                        proccessPayment.getPaymentMethod().getStatus(),
                        BillingStatus.SUCCEEDED.toString(),
                        LocalDateTime.now(ZoneId.of(request.zoneId())),
                        userEntity,
                        savedPeriodEntity
                );
                userEntity.getBillings().add(newBilling);
                userRepository.saveUser(userEntity);
                return SubscriptionServiceHelper.getSubscriptionPlanResponse(
                        subscription.getPlan(),
                        savedPeriodEntity.getEndTime().toString(),
                        subscription.getBillingCycle(),
                        subscription.getStatus());
            }
        } else {
//            Throw payment failed Error
//            since we don't recognize the payment method
            throw new PaymentFailedException("Payment failed!");
        }
        return null;
    }

    @Override
    public Map<String, String> cancelPaidSubscriptionPlan(String userId) {
        UserEntity userEntity = userRepository.findUserById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));

        SubscriptionEntity subscriptionEntity = subscriptionRepository
                .getSubscriptionByUserId(userEntity.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Subscription not found!"));

        subscriptionEntity.updateStatus(SubscriptionStatus.CANCELED.toString());
        subscriptionEntity.updateStartedAt(null);
        subscriptionEntity.updateCanceledAt(LocalDateTime.now(ZoneId.of("Europe/Moscow")));

        userEntity.updatePaymentMethodId(null);

        subscriptionRepository.saveSubscription(subscriptionEntity);
        userRepository.saveUser(userEntity);

        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        return result;
    }

    @Override
    public SubscriptionPlanResponse getSubscriptionPlanByUserId(String userId) {

        PlanEntity planEntity = subscriptionRepository.getPlanByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Plan not found"));

        SubscriptionEntity subscription = planEntity.getSubscription();

        if(subscription != null){
//            Get the current active subscription period
            SubscriptionPeriodEntity periodEntity = subscription.getPeriods()
                    .stream()
                    .filter(period ->
                            period.getStatus().equals(SubscriptionPeriodStatus.ACTIVE.toString()))
                    .findFirst()
                    .orElse(null);

            if (periodEntity != null) {
                return SubscriptionServiceHelper.getSubscriptionPlanResponse(planEntity,
                        periodEntity.getEndTime().toString(),
                        subscription.getBillingCycle(),
                        subscription.getStatus()
                        );
            } else {
                return SubscriptionServiceHelper.getSubscriptionPlanResponse(planEntity,
                        null,null, SubscriptionStatus.CANCELED.toString());
            }
        } else {
            return SubscriptionServiceHelper.getSubscriptionPlanResponse(planEntity,
                    null,null, null);
        }
    }

//    HELPER METHODS
    private SubscriptionEntity getOrCreateSubscriptionEntity(UserEntity userEntity,
                                                             String billingCycle) {
       return subscriptionRepository
                .getSubscriptionByUserId(userEntity.getId())
                .orElseGet(()-> {
                    Subscription subscription = new Subscription(null,
                            SubscriptionStatus.ACTIVE,
                            null,
                            BillingCycle.valueOf(billingCycle),
                            false
                    );
                    return subscriptionRepository
                    .saveSubscription(subscriptionMapper.toEntity(subscription, userEntity));
                });
    }
}