package com.bookit.backend.payload.booking;

import com.bookit.backend.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private UUID bookingId;

    private UUID showId;

    private BookingStatus status;

    private BigDecimal totalAmount;

    private List<BookingSeatResponse> seats;

    private Timestamp createdAt;
}
