package com.bookit.backend.service;

import com.bookit.backend.payload.booking.BookingCreateRequest;
import com.bookit.backend.payload.booking.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface BookingService {
    BookingResponse createBooking(@Valid BookingCreateRequest request);

    List<BookingResponse> getAllBookings();

    BookingResponse getBookingDetails(UUID bookingId);

    String cancelBooking(UUID bookingId);
}
