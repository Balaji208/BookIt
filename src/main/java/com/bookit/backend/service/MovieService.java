package com.bookit.backend.service;

import com.bookit.backend.payload.MovieRequest;
import com.bookit.backend.payload.MovieResponse;
import com.bookit.backend.payload.PageResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface MovieService {
    MovieResponse addMovie(MovieRequest movieRequest);

    MovieResponse updateMovie(MovieRequest movieRequest, UUID movieId);

    PageResponse<MovieResponse> getAllMovies(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    MovieResponse getMovieDetails(UUID movieId);

    MovieResponse deleteMovie(UUID movieId);
}
