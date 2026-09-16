package com.bookit.backend.payload.seat;

import com.bookit.backend.model.SeatType;

import java.math.BigDecimal;
import java.util.UUID;

public class ShowSeatResponse {
    private UUID seatId;

    private String rowLabel;

    private Integer seatNumber;

    private SeatType seatType;

    private BigDecimal price;

    private boolean available;
}
