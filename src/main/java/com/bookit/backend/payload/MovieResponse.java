package com.bookit.backend.payload;

import com.bookit.backend.model.Genre;
import com.bookit.backend.model.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MovieResponse {

    private UUID movieId;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Positive
    private Integer durationMinutes;

    @NotNull
    private Language language;

    @NotNull
    private Genre genre;

    private LocalDate releaseDate;

    private String certificate;

    private String posterUrl;

    @NotNull
    private Boolean active;


    @CreationTimestamp
    private Timestamp createdAt;


    @UpdateTimestamp
    private Timestamp updatedAt;
}
