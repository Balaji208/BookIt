package com.bookit.backend.controller;

import com.bookit.backend.config.AppConstants;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.booking.BookingCreateRequest;
import com.bookit.backend.payload.booking.BookingResponse;
import com.bookit.backend.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v3")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingCreateRequest request) {
        BookingResponse bookingResponse = bookingService.createBooking(request);
        return new ResponseEntity<>(bookingResponse, HttpStatus.CREATED);
    }

    @GetMapping("/bookings")
    public ResponseEntity<PageResponse<BookingResponse>> getAllBookings(
            @RequestParam(
                    name = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER
            )
            @Min(value = 0, message = "Page number cannot be negative")
            Integer pageNumber,

            @RequestParam(
                    name = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE
            )
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 50, message = "Page size cannot exceed 50")
            Integer pageSize,

            @RequestParam(
                    name = "sortBy",
                    defaultValue = "title"
            )
            String sortBy,

            @RequestParam(
                    name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR
            )
            String sortOrder
    ) {
        PageResponse<BookingResponse> bookings = bookingService.getAllBookings(
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingDetails(@PathVariable UUID bookingId) {
        BookingResponse bookingResponse =
                bookingService.getBookingDetails(bookingId);
        return new ResponseEntity<>(bookingResponse, HttpStatus.OK);
    }

    @PatchMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable UUID bookingId) {
        String cancellationResponse =
                bookingService.cancelBooking(bookingId);
        return new ResponseEntity<>(cancellationResponse, HttpStatus.OK);
    }
}
