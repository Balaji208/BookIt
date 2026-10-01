package com.bookit.backend.controller;

import com.bookit.backend.payload.payment.PaymentCreateRequest;
import com.bookit.backend.payload.payment.PaymentResponse;
import com.bookit.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v4")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/bookings/{bookingId}/payments")
    private ResponseEntity<PaymentResponse> createPayment(
            @PathVariable UUID bookingId,
            @RequestBody PaymentCreateRequest paymentCreateRequest
    ) {
        PaymentResponse paymentResponse =
                paymentService.createPayment(
                        bookingId,
                        paymentCreateRequest
                );
        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }
}
