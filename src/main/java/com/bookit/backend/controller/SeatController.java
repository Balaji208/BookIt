package com.bookit.backend.controller;

import com.bookit.backend.config.AppConstants;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.SeatRequest;
import com.bookit.backend.payload.SeatResponse;
import com.bookit.backend.service.SeatService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @PostMapping("/screens/{screenId}/seats")
    public ResponseEntity<SeatResponse> addSeat(@PathVariable UUID screenId,
                                                @RequestBody SeatRequest seatRequest) {
        SeatResponse seatResponse = seatService.addSeat(screenId, seatRequest);
        return new ResponseEntity<>(seatResponse, HttpStatus.OK);
    }

    @PutMapping("/seats/{seatId}")
    public ResponseEntity<SeatResponse> updateSeat(@PathVariable UUID seatId,
                                                   @RequestBody SeatRequest seatRequest) {
        SeatResponse seatResponse = seatService.updateSeat(seatId, seatRequest);
        return new ResponseEntity<>(seatResponse, HttpStatus.OK);
    }

    @DeleteMapping("/seats/{seatId}")
    public ResponseEntity<SeatResponse> deleteSeat(@PathVariable UUID seatId) {
        SeatResponse seatResponse = seatService.deleteSeat(seatId);
        return new ResponseEntity<>(seatResponse, HttpStatus.OK);
    }

    // Customer Operations
    @GetMapping("/screens/{screenId}/seats")
    public ResponseEntity<PageResponse<SeatResponse>> getAllSeatsInScreen(
            @PathVariable UUID screenId,
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
                    defaultValue = "rowLabel"
            )
            String sortBy,

            @RequestParam(
                    name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR
            )
            String sortOrder
    ) {
        PageResponse<SeatResponse> seatResponses = seatService.getAllSeatsInScreen(
                screenId,
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );
        return new ResponseEntity<>(seatResponses, HttpStatus.OK);
    }

    @GetMapping("/seats/{seatId}")
    public ResponseEntity<SeatResponse> getSeatDetails(@PathVariable UUID seatId) {
        SeatResponse seatResponse = seatService.getSeatDetails(seatId);
        return new ResponseEntity<>(seatResponse, HttpStatus.OK);
    }


}
