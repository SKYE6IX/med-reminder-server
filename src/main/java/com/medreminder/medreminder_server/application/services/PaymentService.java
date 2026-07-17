package com.medreminder.medreminder_server.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.loolzaaa.youkassa.client.ApiClient;
import ru.loolzaaa.youkassa.client.ApiClientBuilder;
import ru.loolzaaa.youkassa.model.Payment;
import ru.loolzaaa.youkassa.pojo.Amount;
import ru.loolzaaa.youkassa.pojo.Currency;
import ru.loolzaaa.youkassa.processors.PaymentProcessor;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentProcessor paymentProcessor;


    public PaymentService(@Value("${yookassa.shop.id}") String shopId,
                            @Value("${yookassa.secret.key}") String secretKey) {
        ApiClient apiClient = ApiClientBuilder
                .newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();
        paymentProcessor = new PaymentProcessor(apiClient);
    }

    public Payment processNewPayment(String paymentToken, String amount) {

        String idempotencyKey = UUID.randomUUID().toString();

        return paymentProcessor.create(Payment.builder()
                .amount(Amount.builder().value(amount).currency(Currency.RUB).build())
                .paymentToken(paymentToken)
                .capture(true)
                .savePaymentMethod(true)
                .test(true)
                .build(),
                idempotencyKey
        );
    }

    public Payment processRenewPayment(String paymentMethodId, String amount) {

        String idempotencyKey = UUID.randomUUID().toString();

        return paymentProcessor.create(Payment.builder()
                .amount(Amount.builder().value(amount).currency(Currency.RUB).build())
                .paymentMethodId(paymentMethodId)
                .capture(true)
                .build(),
                idempotencyKey
        );
    }
}
