package com.medreminder.medreminder_server.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.loolzaaa.youkassa.client.ApiClient;
import ru.loolzaaa.youkassa.client.ApiClientBuilder;
import ru.loolzaaa.youkassa.model.Payment;
import ru.loolzaaa.youkassa.pojo.Amount;
import ru.loolzaaa.youkassa.pojo.Currency;
import ru.loolzaaa.youkassa.processors.PaymentProcessor;

@Service
public class PaymentService {

    @Value("${yokassa.shop.id}")
    private String shopId;
    @Value("${yokassa.secret.key}")
    private String secretKey;

    private final PaymentProcessor paymentProcessor;

    public PaymentService() {
        ApiClient apiClient = ApiClientBuilder
                .newBuilder()
                .configureBasicAuth(shopId, secretKey)
                .build();
        paymentProcessor = new PaymentProcessor(apiClient);
    }

    public Payment processPayment(String paymentToken, String amount) {

        return paymentProcessor.create(Payment.builder()
                .amount(Amount.builder().value(amount).currency(Currency.RUB).build())
                .paymentToken(paymentToken)
                .capture(true)
                .savePaymentMethod(true)
                .test(true)
                .build(), null
        );
    }
}
