package com.medreminder.medreminder_server.domain.models.billing;

import com.medreminder.medreminder_server.domain.models.users.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private final String id;
    private final BigDecimal amount;
    private final String method;
    private PaymentStatus status;
    private LocalDateTime paidAt;

    private final User user;
    private final SubscriptionPeriod subscriptionPeriod;

    public Payment(String id,
                   BigDecimal amount,
                   String method,
                   PaymentStatus status,
                   User user,
                   SubscriptionPeriod subscriptionPeriod) {
        this.id = id;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.user = user;
        this.subscriptionPeriod = subscriptionPeriod;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public PaymentStatus getPaymentStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public SubscriptionPeriod getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.status = paymentStatus;
    }

    public void updatePaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
