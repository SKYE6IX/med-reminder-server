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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final SubscriptionMapper subscriptionMapper;

//    TODO:
//    A scheduler that flip Plan from PRO to FREE.
//    > This will look for subscription period that the current day
//    has passed the endTime and status still ACTIVE.
//    > If found, we check if the subscription status is cancelled
//    > If cancelled? we flip the Plan from PRO to FREE.
//    > and flip the Period status to Completed.

//    A scheduler that renew Subscription.
//    > This will look for subscription period which the current day
//      is the same as the end time OR is greater than it.(RETRY CASE FOR FAILED PAYMENT)
//      and status still ACTIVE.
//    > If found, we check if the subscription is still in active status,
//      meaning user hasn't canceled.
//    > We then get the paymentMethodId that is stored in the user data
//    > We create a payment with it.
//    > if succeeded, we create a new subscription period, and flip the
//      status of the old one to Completed
//    > same process will repeat if payment failed and only retry twice.
//    > if failed the second time, we flip the Subscription to Cancel.


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
        SubscriptionPeriodEntity periodEntity = createSubscriptionPeriod(
                subscription,request.billingCycle(),request.zoneId()
        );

//        Start a payment process
//        If payment method is BANK_CARD, we can process all in one go.
        if(request.paymentMethod().equals("BANK_CARD")){
            BigDecimal amount = new BigDecimal(request.amount());
            BigDecimal paddedAmount = amount.setScale(2, RoundingMode.HALF_UP);

            Payment proccessPayment = paymentService.processPayment(request.paymentToken(),
                    paddedAmount.toPlainString());

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
                        new BigDecimal(request.amount()),
                        proccessPayment.getPaymentMethod().getStatus(),
                        BillingStatus.SUCCEEDED.toString(),
                        LocalDateTime.now(ZoneId.of(request.zoneId())),
                        userEntity,
                        savedPeriodEntity
                );
                userEntity.getBillings().add(newBilling);
                userRepository.saveUser(userEntity);
                return getSubscriptionPlanResponse(subscription.getPlan(),
                        savedPeriodEntity.getEndTime().toString(),
                        subscription.getBillingCycle(),
                        subscription.getStatus());
            }
        } else if (request.paymentMethod().equals("SBP")) {
//            Here we handle the payment process based on the
//            status payment won't be immediately return to us.
            return null;
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
        subscriptionEntity.updateCanceledAt(LocalDateTime.now());

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
                return getSubscriptionPlanResponse(planEntity,
                        periodEntity.getEndTime().toString(),
                        subscription.getBillingCycle(),
                        subscription.getStatus()
                        );
            } else {
                return getSubscriptionPlanResponse(planEntity,
                        null,null,SubscriptionStatus.CANCELED.toString());
            }
        } else {
            return getSubscriptionPlanResponse(planEntity,
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
                            true
                    );
                    return subscriptionRepository
                    .saveSubscription(subscriptionMapper.toEntity(subscription, userEntity));
                });
    }

    private SubscriptionPeriodEntity createSubscriptionPeriod(
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

    private SubscriptionPlanResponse getSubscriptionPlanResponse(PlanEntity planEntity,
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
}
