package com.bookit.backend.payload;

import com.bookit.backend.model.Genre;
import com.bookit.backend.model.Language;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Positive
    private Integer durationMinutes;

    @NotNull
    private Genre genre;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Language language;

    private LocalDate releaseDate;

    private String certificate;

    private String posterUrl;
}