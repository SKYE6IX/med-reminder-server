package com.medreminder.medreminder_server.application.exceptions;

import org.springframework.http.HttpStatus;

public class PaymentFailedException extends BaseException {

    public PaymentFailedException(String message) {
        super(message, HttpStatus.PAYMENT_REQUIRED, "PAYMENT_REQUIRED!");
    }
}
