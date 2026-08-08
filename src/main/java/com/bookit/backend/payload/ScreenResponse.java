package com.bookit.backend.payload;

import com.bookit.backend.model.ScreenType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScreenResponse {
    private UUID screenId;

    private UUID theatreId;

    private String name;

    private Integer totalSeats;

    private ScreenType screenType;

    private Boolean active;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
