package com.bookit.backend.payload;

import com.bookit.backend.model.ShowStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShowRequest {
    @NotNull
    private UUID movieId;

    @NotNull
    private UUID screenId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    @Positive
    private BigDecimal basePrice;

    private ShowStatus status;

}
