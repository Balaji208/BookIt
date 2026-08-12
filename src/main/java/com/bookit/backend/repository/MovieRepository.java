package com.bookit.backend.repository;

import com.bookit.backend.model.Movie;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {


    Optional<Movie> findByMovieIdAndActiveTrue(UUID movieId);

    Page<Movie> findAllByActiveTrue(Pageable pageDetails);

    boolean existsByTitleAndReleaseDateAndActiveTrue(
            String title,
            LocalDate releaseDate);
}
