package com.bookit.backend.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentGatewayResponse {
    private final boolean successful;
    private final String transactionReference;
}
