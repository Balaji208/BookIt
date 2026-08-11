package com.bookit.backend.controller;

import com.bookit.backend.model.Seat;
import com.bookit.backend.payload.SeatRequest;
import com.bookit.backend.payload.SeatResponse;
import com.bookit.backend.service.SeatService;
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
    public ResponseEntity<List<SeatResponse>> getAllSeatsInScreen(@PathVariable UUID screenId) {
        List<SeatResponse> seatResponses = seatService.getAllSeatsInScreen(screenId);
        return new ResponseEntity<>(seatResponses, HttpStatus.OK);
    }

    @GetMapping("/seats/{seatId}")
    public ResponseEntity<SeatResponse> getSeatDetails(@PathVariable UUID seatId) {
        SeatResponse seatResponse = seatService.getSeatDetails(seatId);
        return new ResponseEntity<>(seatResponse, HttpStatus.OK);
    }


}
