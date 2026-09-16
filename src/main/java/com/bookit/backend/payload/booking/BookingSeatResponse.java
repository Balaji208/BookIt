package com.bookit.backend.payload.booking;
import com.bookit.backend.model.SeatType;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingSeatResponse {

    private UUID seatId;
    private String rowLabel;
    private Integer seatNumber;
    private SeatType seatType;
    private BigDecimal price;

}
