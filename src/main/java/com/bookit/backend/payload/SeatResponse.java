package com.bookit.backend.payload;

import com.bookit.backend.model.SeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeatResponse {
    private UUID seatId;

    private UUID screenId;

    private String rowLabel;

    private Integer  seatNumber;

    private SeatType seatType;

    private Boolean active;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
