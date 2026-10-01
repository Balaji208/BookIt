package com.bookit.backend.payload.payment;

import com.bookit.backend.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequest {
    private PaymentMethod paymentMethod;
}
