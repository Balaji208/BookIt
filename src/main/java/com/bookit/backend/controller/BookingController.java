package com.bookit.backend.controller;

import com.bookit.backend.payload.booking.BookingCreateRequest;
import com.bookit.backend.payload.booking.BookingResponse;
import com.bookit.backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @PathVariable BookingCreateRequest request) {
        BookingResponse bookingResponse = bookingService.createBooking(request);
        return new ResponseEntity<>(bookingResponse, HttpStatus.CREATED);
    }

}
