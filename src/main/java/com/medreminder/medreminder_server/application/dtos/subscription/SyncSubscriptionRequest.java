package com.medreminder.medreminder_server.application.dtos.subscription;

public record SyncSubscriptionRequest(boolean willRenew,
                                      long latestPurchaseDate,
                                      long expirationDate,
                                      long unsubscribeDetectedAt,
                                      String zoneId) {
}
