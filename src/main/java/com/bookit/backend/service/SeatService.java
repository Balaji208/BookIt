package com.bookit.backend.service;

import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.SeatRequest;
import com.bookit.backend.payload.SeatResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface SeatService {
    SeatResponse addSeat(UUID screenId, SeatRequest seatRequest);

    SeatResponse updateSeat(UUID seatId, SeatRequest seatRequest);

    SeatResponse deleteSeat(UUID seatId);

    PageResponse<SeatResponse> getAllSeatsInScreen(
            UUID screenId,
            @Min(value = 0, message = "Page number cannot be negative") Integer pageNumber,
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 50, message = "Page size cannot exceed 50")
            Integer pageSize,
            String sortBy,
            String sortOrder
    );

    SeatResponse getSeatDetails(UUID seatId);
}
