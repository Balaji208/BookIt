package com.bookit.backend.service;

import com.bookit.backend.payload.SeatRequest;
import com.bookit.backend.payload.SeatResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface SeatService {
    SeatResponse addSeat(UUID screenId, SeatRequest seatRequest);

    SeatResponse updateSeat(UUID seatId, SeatRequest seatRequest);

    SeatResponse deleteSeat(UUID seatId);

    List<SeatResponse> getAllSeatsInScreen(UUID screenId);

    SeatResponse getSeatDetails(UUID seatId);
}
