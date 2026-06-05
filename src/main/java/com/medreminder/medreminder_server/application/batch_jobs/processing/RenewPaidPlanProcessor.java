package com.medreminder.medreminder_server.application.batch_jobs.processing;

import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.domain.models.billing.BillingCycle;
import com.medreminder.medreminder_server.domain.models.billing.BillingStatus;
import com.medreminder.medreminder_server.domain.models.subscription.SubscriptionPeriodStatus;
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





//TODO:
// Handle failed payment to be retried again the next two days
// and cancel subscription after that.
public class RenewPaidPlanProcessor implements ItemProcessor<SubscriptionPeriodEntity, RenewPaidPlanResult> {

    private final PaymentService paymentService;

    public RenewPaidPlanProcessor(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public RenewPaidPlanResult process(SubscriptionPeriodEntity subscriptionPeriodEntity) {

        SubscriptionEntity subscriptionEntity = subscriptionPeriodEntity.getSubscription();

        UserEntity userEntity = subscriptionEntity.getUser();
//        First we start with the process of Payment, we return null
//        if payment failed also we can send a message here to user
//        about their failed payment
        final String billingAmount = SubscriptionServiceHelper
                .getBillingCycleAmount(BillingCycle.valueOf(subscriptionEntity.getBillingCycle()));
        if (billingAmount == null) {
            return null;
        }

        Payment processRenewPayment = paymentService
                .processRenewPayment(userEntity.getPaymentMethodId(), billingAmount);

        if(processRenewPayment == null || processRenewPayment.getStatus().equals(Payment.Status.CANCELED)){
//            Send mail to user about unsuccessful payment.
//            And figure out on how to try again by tracking how many times it has run.
//            before we flip and cancel the subscription.
            return null;
        } else if (processRenewPayment.getStatus().equals(Payment.Status.SUCCEEDED)) {

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
//            get it from saved data and then attached it before saving.
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