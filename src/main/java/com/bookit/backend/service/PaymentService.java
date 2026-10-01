package com.bookit.backend.service;

import com.bookit.backend.payload.payment.PaymentCreateRequest;
import com.bookit.backend.payload.payment.PaymentResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface PaymentService {
    PaymentResponse createPayment(
            UUID bookingId,
            PaymentCreateRequest paymentCreateRequest);
}
