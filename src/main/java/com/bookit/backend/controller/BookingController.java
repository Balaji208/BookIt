package com.bookit.backend.controller;

import com.bookit.backend.payload.booking.BookingCreateRequest;
import com.bookit.backend.payload.booking.BookingResponse;
import com.bookit.backend.service.BookingService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingDetails(@PathVariable UUID bookingId) {
        BookingResponse bookingResponse =
                bookingService.getBookingDetails(bookingId);
        return new ResponseEntity<>(bookingResponse, HttpStatus.OK);
    }

    @PatchMapping("/api/v3/bookings/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable UUID bookingId) {
        String cancellationResponse =
                bookingService.cancelBooking(bookingId);
        return new ResponseEntity<>(cancellationResponse, HttpStatus.OK);
    }
}
