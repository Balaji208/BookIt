package com.bookit.backend.payload;

import com.bookit.backend.model.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SeatRequest {
    @NotBlank
    private String rowLabel;

    @NotNull
    @Positive
    private Integer seatNumber;

    @NotNull
    private SeatType seatType;
}
