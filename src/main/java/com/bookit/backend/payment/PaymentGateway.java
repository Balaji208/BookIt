package com.bookit.backend.payment;

import com.bookit.backend.model.PaymentMethod;
import com.bookit.backend.payload.payment.PaymentResponse;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentGatewayResponse  processPayment(
            BigDecimal amount,
            PaymentMethod paymentMethod
    );
}
