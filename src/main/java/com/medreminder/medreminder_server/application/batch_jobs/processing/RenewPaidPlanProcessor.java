package com.medreminder.medreminder_server.application.batch_jobs.processing;

import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.domain.models.billing.BillingCycle;
import com.medreminder.medreminder_server.domain.models.billing.BillingStatus;
import com.medreminder.medreminder_server.domain.models.subscription.SubscriptionPeriodStatus;
import com.medreminder.medreminder_server.domain.models.subscription.SubscriptionStatus;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionServiceHelper;
import com.medreminder.medreminder_server.domain.services.subscription.SubscriptionServiceImpl;
import com.medreminder.medreminder_server.infrastructure.entity.billing.BillingEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import ru.loolzaaa.youkassa.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class RenewPaidPlanProcessor implements ItemProcessor<SubscriptionPeriodEntity, RenewPaidPlanResult> {

    private final PaymentService paymentService;

    public RenewPaidPlanProcessor(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public RenewPaidPlanResult process(SubscriptionPeriodEntity subscriptionPeriodEntity) {

        SubscriptionEntity subscriptionEntity = subscriptionPeriodEntity.getSubscription();

        UserEntity userEntity = subscriptionEntity.getUser();

        Payment processRenewPayment;

        final LocalDateTime now = LocalDateTime.now(ZoneId.of(subscriptionEntity.getTimeZone()));

        final String billingAmount = SubscriptionServiceHelper
                .getBillingCycleAmount(BillingCycle.valueOf(subscriptionEntity.getBillingCycle()));

        if (billingAmount == null) {
            return null;
        }

//        Return early if subscription last payment failed.
//          And the next retry day isn't now.
//        By return null, it to indicate worker not to do anything.
        if(subscriptionEntity.getBillingRetry()
                && !subscriptionEntity.getNextRetryBillingAt().isBefore(now)) {
//            Don't do anything for the particular subscription.
            return null;
        } else if(subscriptionEntity.getBillingRetry()
                && subscriptionEntity.getNextRetryBillingAt().isBefore(now) ){
//            We retry payment again.
            processRenewPayment = paymentService
                    .processRenewPayment(userEntity.getPaymentMethodId(), billingAmount);
        } else {
//            A fresh renew payment
            processRenewPayment = paymentService
                    .processRenewPayment(userEntity.getPaymentMethodId(), billingAmount);
        }

//        We handled failed payment here.
//        We either allowed a retry for second time, or canceled the
//        subscription if the second time already used.
        if(processRenewPayment == null
                || processRenewPayment.getStatus().equals(Payment.Status.CANCELED)) {
//            Meaning: failed payment is a retry one.
            if(subscriptionEntity.getBillingRetry()){
                subscriptionEntity.updateStatus(SubscriptionStatus.CANCELED.toString());
                subscriptionEntity.updateStartedAt(null);
                subscriptionEntity.updateCanceledAt(now);
                subscriptionEntity.updateIsBillingRetry(false);
                subscriptionEntity.updateNextRetryBillingAt(null);
                subscriptionEntity.getPeriods()
                        .stream()
                        .filter(period -> period.getId().equals(subscriptionPeriodEntity.getId()))
                        .findFirst()
                        .ifPresent(period ->
                                period.updateStatus(SubscriptionPeriodStatus.COMPLETED.toString()));
            } else {
//                Meaning: failed payment isn't a retry one
                subscriptionEntity.updateIsBillingRetry(true);
//                Retry again in next two days.
                subscriptionEntity.updateNextRetryBillingAt(now.plusDays(2)
                        .truncatedTo(ChronoUnit.SECONDS));
            }

            return new RenewPaidPlanResult(subscriptionEntity, null);

        } else if (processRenewPayment.getStatus().equals(Payment.Status.SUCCEEDED)) {

            subscriptionEntity.updateIsBillingRetry(false);
            subscriptionEntity.updateNextRetryBillingAt(null);
//           Update the previous period status
            subscriptionEntity.getPeriods()
                    .stream()
                    .filter(period -> period.getId().equals(subscriptionPeriodEntity.getId()))
                    .findFirst()
                    .ifPresent(period ->
                            period.updateStatus(SubscriptionPeriodStatus.COMPLETED.toString()));

//            Create a new period for the renewal
            SubscriptionPeriodEntity newPeriodEntity = SubscriptionServiceHelper.createSubscriptionPeriod(
                    subscriptionEntity, subscriptionEntity.getBillingCycle(), subscriptionEntity.getTimeZone()
            );

            subscriptionEntity.getPeriods().add(newPeriodEntity);

//            Create a billing. We set subscription period to null here, for later we
//            get it from saved data and then attached it to billing before saving.
            BillingEntity newBilling = new BillingEntity(
                    null,
                    new BigDecimal(billingAmount),
                    processRenewPayment.getPaymentMethod().getStatus(),
                    BillingStatus.SUCCEEDED.toString(),
                    LocalDateTime.now(ZoneId.of(subscriptionEntity.getTimeZone())),
                    userEntity,
                    null
            );

            return new RenewPaidPlanResult(subscriptionEntity, newBilling);
        }

//        We return null and do nothing if none of the above
        return null;
    }
}