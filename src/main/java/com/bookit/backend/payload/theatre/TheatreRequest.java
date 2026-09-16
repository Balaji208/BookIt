package com.bookit.backend.payload.theatre;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
