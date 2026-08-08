package com.bookit.backend.payload;

import com.bookit.backend.model.Language;
import com.bookit.backend.model.ShowStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShowResponse {

    private UUID showId;

    private UUID movieId;
    private String movieTitle;

    private UUID screenId;
    private String screenName;

    private UUID theatreId;
    private String theatreName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private BigDecimal basePrice;


    private ShowStatus status;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}