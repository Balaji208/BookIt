package com.bookit.backend.service;

import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.booking.BookingCreateRequest;
import com.bookit.backend.payload.booking.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface BookingService {
    BookingResponse createBooking(@Valid BookingCreateRequest request);

    PageResponse<BookingResponse> getAllBookings(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    );

    BookingResponse getBookingDetails(UUID bookingId);

    String cancelBooking(UUID bookingId);
}
