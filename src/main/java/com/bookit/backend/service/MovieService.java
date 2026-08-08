package com.bookit.backend.service;

import com.bookit.backend.payload.MovieRequest;
import com.bookit.backend.payload.MovieResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface MovieService {
    MovieResponse addMovie(MovieRequest movieRequest);

    MovieResponse updateMovie(MovieRequest movieRequest, UUID movieId);

    List<MovieResponse> getAllMovies();

    MovieResponse getMovieDetails(UUID movieId);

    MovieResponse deleteMovie(UUID movieId);
}
