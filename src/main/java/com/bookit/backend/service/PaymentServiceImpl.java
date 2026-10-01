package com.bookit.backend.service;

import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.*;
import com.bookit.backend.payload.payment.PaymentCreateRequest;
import com.bookit.backend.payload.payment.PaymentResponse;
import com.bookit.backend.payment.PaymentGateway;
import com.bookit.backend.payment.PaymentGatewayResponse;
import com.bookit.backend.repository.BookingRepository;
import com.bookit.backend.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor

public class PaymentServiceImpl implements PaymentService{

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final UserService userService;


    @Override
    @Transactional
    public PaymentResponse createPayment(
            UUID bookingId,
            PaymentCreateRequest paymentCreateRequest
    ) {
        // 1. Validate parameters
        if(paymentCreateRequest == null) {
            log.debug(
                    "Received null for paymentCreateRequest with booking id :{}",
                    bookingId
            );
            throw new IllegalArgumentException(
                    "Payment request cannot be null"
            );
        }
        if (bookingId == null) {
            throw new IllegalArgumentException(
                    "Booking id cannot be null"
            );
        }

        log.debug(
                "Creating payment for booking id: {}",
                bookingId
        );


        // 2. Fetch booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow( () -> {
                   log.debug("Booking not found for id : {}", bookingId);
                   return new ResourceNotFoundException(
                           "Booking",
                           "BookingId",
                           bookingId.toString()
                   );
                });

        // 3. Validate booking status
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Payment cannot be initiated for booking with status: "
                            + booking.getStatus()
            );
        }

        User loggedInUser = userService.getCurrentUser();
        if (!booking.getUser().getUserId().equals(loggedInUser.getUserId())) {
            throw new AccessDeniedException(
                    "You do not have access to this booking"
            );
        }

            // 4. Create payment
        Payment payment = new Payment();

        payment.setPaymentMethod(
                paymentCreateRequest.getPaymentMethod()
        );
        // Gateway will provide this after processing
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(booking.getTotalAmount());
        payment.setBooking(booking);
        payment.setTransactionReference(null);

        // 5. Save payment object
        Payment savedPayment = paymentRepository.save(payment);

        log.debug(
                "Payment created with id: {} for booking id: {}",
                savedPayment.getPaymentId(),
                bookingId
        );

        // 5. Process payment through gateway
        PaymentGatewayResponse gatewayResponse =
                paymentGateway.processPayment(
                        savedPayment.getAmount(),
                        savedPayment.getPaymentMethod()
                );

        // 6. Update payment based on gateway result
        if (gatewayResponse.isSuccessful()) {

            savedPayment.setPaymentStatus(
                    PaymentStatus.SUCCESS
            );

            savedPayment.setTransactionReference(
                    gatewayResponse.getTransactionReference()
            );

            booking.setStatus(BookingStatus.CONFIRMED);

            log.debug(
                    "Payment successful for booking id: {}",
                    bookingId
            );

        } else {

            savedPayment.setPaymentStatus(
                    PaymentStatus.FAILED
            );

            savedPayment.setTransactionReference(
                    gatewayResponse.getTransactionReference()
            );

            log.debug(
                    "Payment failed for booking id: {}",
                    bookingId
            );
        }



        // 8. Build response
        return mapToPaymentResponse(savedPayment);

    }

    private PaymentResponse mapToPaymentResponse(
            Payment payment
    ) {

        PaymentResponse response = new PaymentResponse();

        response.setPaymentId(
                payment.getPaymentId()
        );

        response.setBookingId(
                payment.getBooking().getBookingId()
        );

        response.setAmount(
                payment.getAmount()
        );

        response.setPaymentStatus(
                payment.getPaymentStatus()
        );

        response.setPaymentMethod(
                payment.getPaymentMethod()
        );

        response.setTransactionReference(
                payment.getTransactionReference()
        );

        response.setCreatedAt(
                payment.getCreatedAt()
        );

        response.setUpdatedAt(
                payment.getUpdatedAt()
        );

        return response;
    }
}
