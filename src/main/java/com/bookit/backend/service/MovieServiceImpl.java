package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Movie;
import com.bookit.backend.payload.MovieRequest;
import com.bookit.backend.payload.MovieResponse;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.repository.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService{

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MovieResponse addMovie(MovieRequest movieRequest) {
        log.debug("Creating movie title={}", movieRequest.getTitle());

        // check if movie already exists
        if(movieRepository.existsByTitleAndReleaseDateAndActiveTrue(movieRequest.getTitle(), movieRequest.getReleaseDate())) {
            log.warn("Movie already exists : title={} , releaseDate={}",
                    movieRequest.getTitle(),
                    movieRequest.getReleaseDate()
            );
            throw new APIException("Movie already exists");
        }
        Movie movie = modelMapper.map(movieRequest, Movie.class);
        Movie savedMovie = movieRepository.save(movie);

        log.info("Movie created successfully movieId={}", savedMovie.getMovieId());
        return modelMapper.map(savedMovie, MovieResponse.class);
    }

    @Transactional
    @Override
    public MovieResponse updateMovie(MovieRequest movieRequest, UUID movieId) {
        log.debug("Updating movie movieId={}", movieId);

        // check if movie exists
        Movie savedMovie = movieRepository.findByMovieIdAndActiveTrue(movieId)
                .orElseThrow(() -> {
                    log.warn("Movie not found or deactivated movieId={}", movieId);
                    return new ResourceNotFoundException("Movie", "MovieId", movieId);
                });

        log.debug("Movie found: movieId={}", movieId);

        savedMovie.setTitle(movieRequest.getTitle());
        savedMovie.setDescription(movieRequest.getDescription());
        savedMovie.setDurationMinutes(movieRequest.getDurationMinutes());
        savedMovie.setPosterUrl(movieRequest.getPosterUrl());
        savedMovie.setCertificate(movieRequest.getCertificate());
        savedMovie.setLanguage(movieRequest.getLanguage());
        savedMovie.setReleaseDate(movieRequest.getReleaseDate());

        log.info("Movie updated successfully movieId={}", movieId);
        return modelMapper.map(savedMovie, MovieResponse.class);
    }

    @Override
    public PageResponse<MovieResponse> getAllMovies(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        log.debug(
                "Fetching movies page={}, size={}, sortBy={}, sortOrder={}",
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );

        // define sort order
        Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortAndOrder);
        Page<Movie> moviesPage = movieRepository.findAllByActiveTrue(pageDetails);

        if(moviesPage.isEmpty()) {
            log.warn("No movie found!");
            throw new APIException("No movies are there!");
        }
        List<MovieResponse> movieResponses = moviesPage.
                stream().
                map((movie) ->
                        modelMapper.map(movie, MovieResponse.class))
                .toList();

        PageResponse<MovieResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(movieResponses);
        pageResponse.setPageSize(moviesPage.getSize());
        pageResponse.setPageNumber(moviesPage.getNumber());
        pageResponse.setTotalPages(moviesPage.getTotalPages());
        pageResponse.setTotalElements(moviesPage.getTotalElements());

        log.info(
                "Movies fetched successfully: page={}, size={}, totalElements={}",
                moviesPage.getNumber(),
                moviesPage.getSize(),
                moviesPage.getTotalElements()
        );

        return pageResponse;
    }

    @Override
    public MovieResponse getMovieDetails(UUID movieId) {
        log.debug("Fetching movie details movieId={}", movieId);

        Movie savedMovie = movieRepository.findByMovieIdAndActiveTrue(movieId)
                .orElseThrow(() -> {
                    log.warn("Movie not found  or deactivated movieId={}", movieId);
                    return new ResourceNotFoundException("Movie", "MovieId", movieId);
                });

        log.debug("Movie details fetched successfully movieId={}", movieId);

        return modelMapper.map(savedMovie, MovieResponse.class);
    }

    @Transactional
    @Override
    public MovieResponse deleteMovie(UUID movieId) {
        log.debug("Deactivating movie movieId={}", movieId);

        Movie savedMovie = movieRepository.findByMovieIdAndActiveTrue(movieId)
                .orElseThrow(() -> {
                    log.warn("Movie not found or deactivated  movieId={}", movieId);
                    return new ResourceNotFoundException("Movie", "MovieId", movieId);
                });

        savedMovie.setActive(false);
        log.info("Movie deactivated successfully! movieId={}", movieId);
        return modelMapper.map(savedMovie, MovieResponse.class);
    }
}
