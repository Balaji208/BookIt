package com.bookit.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "movie_id")
    private UUID movieId;

    @NotNull
    private String title;

    private String description;

    @NotNull
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Language  language;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private String certificate;
    @Column(name = "poster_url")

    private String posterUrl;

    @NotNull
    private Boolean active;


    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;


    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    // Relationships
    @OneToMany(mappedBy = "movie")
    private List<Show> shows = new ArrayList<>();
}
