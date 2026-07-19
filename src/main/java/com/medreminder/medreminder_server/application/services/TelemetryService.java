package com.medreminder.medreminder_server.application.services;

import io.sentry.Sentry;
import io.sentry.SentryLogLevel;
import io.sentry.logger.SentryLogParameters;
import io.sentry.SentryAttribute;
import io.sentry.SentryAttributes;

import java.util.Map;

public class TelemetryService {

    private TelemetryService(){}

    public static void captureException(Exception e) {
        Sentry.captureException(e);
    }

    public static void log(SentryLogLevel level, String message, SentryAttribute... attributes) {
        Sentry.logger().log(
                level,
                SentryLogParameters.create(SentryAttributes.of(attributes)),
                message
        );
    }

    public static void log(SentryLogLevel level, String message, Map<String, Object> attributes) {
        SentryAttribute[] sentryAttributes = attributes.entrySet().stream()
                .map(e -> SentryAttribute.named(e.getKey(), e.getValue()))
                .toArray(SentryAttribute[]::new);

        log(level, message, sentryAttributes);
    }
}
