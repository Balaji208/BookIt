package com.bookit.backend.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TheatreRequest {
    @NotNull
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String city;

    private Double latitude;
    private Double longitude;
}
