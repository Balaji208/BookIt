package com.bookit.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TheatreResponse {
    private UUID theatreId;

    private String name;

    private String city;

    private String address;

    private Double latitude;

    private Double longitude;

    private Boolean active;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
