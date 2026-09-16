package com.bookit.backend.service;

import com.bookit.backend.payload.booking.BookingCreateRequest;
import com.bookit.backend.payload.booking.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface BookingService {
    BookingResponse createBooking(@Valid BookingCreateRequest request);
}
