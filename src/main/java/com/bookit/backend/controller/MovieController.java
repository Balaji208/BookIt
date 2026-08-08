package com.bookit.backend.controller;

import com.bookit.backend.model.Movie;
import com.bookit.backend.payload.MovieRequest;
import com.bookit.backend.payload.MovieResponse;
import com.bookit.backend.service.MovieService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ModelMapper modelMapper;

    // Admin Operations

    @PostMapping("/movies")
    public ResponseEntity<MovieResponse> addMovie(@RequestBody MovieRequest movieRequest) {
        MovieResponse movieResponse = movieService.addMovie(movieRequest);
        return new ResponseEntity<>(movieResponse, HttpStatus.CREATED);
    }

    @PutMapping("/movies/{movieId}")
    public ResponseEntity<MovieResponse> updateMovie(
            @RequestBody MovieRequest movieRequest,
            @PathVariable UUID movieId) {
        MovieResponse movieResponse = movieService.updateMovie(movieRequest, movieId);
        return new ResponseEntity<>(movieResponse, HttpStatus.OK);
    }

    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<MovieResponse> deleteMovie(@PathVariable UUID movieId) {
        MovieResponse movieResponse = movieService.deleteMovie(movieId);
        return new ResponseEntity<>(movieResponse, HttpStatus.OK);
    }

    // Customer operations

    @GetMapping("/movies")
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        List<MovieResponse> movieResponses = movieService.getAllMovies();
        return new ResponseEntity<>(movieResponses, HttpStatus.OK);
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<MovieResponse> getMovieDetails(@PathVariable UUID movieId) {
        MovieResponse movieResponse = movieService.getMovieDetails(movieId);
        return new ResponseEntity<>(movieResponse, HttpStatus.OK);
    }
}
