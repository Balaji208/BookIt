package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Movie;
import com.bookit.backend.payload.MovieRequest;
import com.bookit.backend.payload.MovieResponse;
import com.bookit.backend.repository.MovieRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MovieServiceImpl implements MovieService{

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MovieResponse addMovie(MovieRequest movieRequest) {
        // check if movie already exists
        if(movieRepository.existsByTitleAndReleaseDate(movieRequest.getTitle(), movieRequest.getReleaseDate()))
            throw new APIException("Movie already exists");
        Movie movie = modelMapper.map(movieRequest, Movie.class);
        Movie savedMovie = movieRepository.save(movie);
        return modelMapper.map(savedMovie, MovieResponse.class);
    }

    @Transactional
    @Override
    public MovieResponse updateMovie(MovieRequest movieRequest, UUID movieId) {
        // check if movie exists
        Movie savedMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "MovieId", movieId));

        savedMovie.setTitle(movieRequest.getTitle());
        savedMovie.setDescription(movieRequest.getDescription());
        savedMovie.setDurationMinutes(movieRequest.getDurationMinutes());
        savedMovie.setPosterUrl(movieRequest.getPosterUrl());
        savedMovie.setCertificate(movieRequest.getCertificate());
        savedMovie.setLanguage(movieRequest.getLanguage());
        savedMovie.setReleaseDate(movieRequest.getReleaseDate());

        return modelMapper.map(savedMovie, MovieResponse.class);
    }

    @Override
    public List<MovieResponse> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream().map((movie) ->{
           return modelMapper.map(movie, MovieResponse.class);
        }).toList();
    }
}
