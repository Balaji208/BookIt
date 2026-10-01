package com.bookit.backend.payment;

import com.bookit.backend.model.PaymentMethod;
import com.bookit.backend.payload.payment.PaymentResponse;

import java.math.BigDecimal;
import java.util.UUID;

public class FakePaymentGateway implements PaymentGateway{

    @Override
    public PaymentGatewayResponse processPayment(
            BigDecimal amount,
            PaymentMethod paymentMethod) {
        String transactionReference =
                "FAKE-TXN-" + UUID.randomUUID();

        return new PaymentGatewayResponse(
                true,
                transactionReference
        );
    }
}
