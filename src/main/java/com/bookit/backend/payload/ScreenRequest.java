package com.bookit.backend.payload;

import com.bookit.backend.model.ScreenType;
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
public class ScreenRequest {
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Integer totalSeats;

    @NotNull
    private ScreenType screenType;
}
