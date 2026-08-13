package com.medreminder.medreminder_server.domain.services.subscription;

import com.medreminder.medreminder_server.domain.models.subscription.*;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionEntity;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SubscriptionServiceHelper {

    private final static SubscriptionMapper subscriptionMapper = new SubscriptionMapper();

    public static SubscriptionPeriodEntity createSubscriptionPeriod(
            SubscriptionEntity subscriptionEntity,
            long latestPurchaseDate,
            long expirationDate,
            String zoneId ) {
        ZoneId zone = ZoneId.of(zoneId);

        LocalDateTime startAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(latestPurchaseDate),
                zone
        );
        LocalDateTime endAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(expirationDate),
                zone
        );

        SubscriptionPeriod newPeriod = new SubscriptionPeriod(
                null,
                startAt,
                endAt,
                SubscriptionPeriodStatus.ACTIVE
        );

        return subscriptionMapper.toEntity(newPeriod, subscriptionEntity);
    }
}
