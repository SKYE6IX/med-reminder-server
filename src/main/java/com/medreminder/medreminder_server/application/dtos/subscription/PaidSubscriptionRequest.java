package com.medreminder.medreminder_server.application.dtos.subscription;

public record PaidSubscriptionRequest(String paymentToken,
                                      String paymentMethod,
                                      String amount,
                                      String billingCycle,
                                      String zoneId) {
}
