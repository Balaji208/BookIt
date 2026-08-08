package com.bookit.backend.repository;

import com.bookit.backend.model.Movie;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    boolean existsByTitleAndReleaseDate(@NotBlank String title, LocalDate releaseDate);
}
