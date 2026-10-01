package com.bookit.backend.payload.payment;

import com.bookit.backend.model.PaymentMethod;
import com.bookit.backend.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID paymentId;

    private UUID bookingId;

    private BigDecimal amount;

    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;

    private String transactionReference;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
