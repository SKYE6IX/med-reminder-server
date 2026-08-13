package com.medreminder.medreminder_server.application.dtos.subscription;

public record PaidSubscriptionRequest(long originalPurchaseDate,
                                      long latestPurchaseDate,
                                      long expirationDate,
                                      String store,
                                      String zoneId) {
}
