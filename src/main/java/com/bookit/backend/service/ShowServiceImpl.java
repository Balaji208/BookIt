package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Movie;
import com.bookit.backend.model.Screen;
import com.bookit.backend.model.Show;
import com.bookit.backend.model.ShowStatus;
import com.bookit.backend.payload.ShowRequest;
import com.bookit.backend.payload.ShowResponse;
import com.bookit.backend.repository.MovieRepository;
import com.bookit.backend.repository.ScreenRepository;
import com.bookit.backend.repository.ShowRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShowServiceImpl implements ShowService{

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public ShowResponse addShow(ShowRequest showRequest) {
        // Validate time range
        if (!showRequest.getStartTime().isBefore(showRequest.getEndTime())) {
            throw new APIException("Start time must be before end time");
        }

        Screen screen = screenRepository.findById(showRequest.getScreenId())
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Screen",
                                "ScreenId",
                                showRequest.getScreenId()));
        Movie movie = movieRepository.findById(showRequest.getMovieId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Movie",
                                "MovieId",
                                showRequest.getMovieId()));

        // Check for overlapping shows on this screen
        // NOTE : This below approach is not scalable we'll replace it with SQL based Overlap checking in future

//        List<Show> shows = screen.getShows();
//        LocalDateTime currentStartTime = showRequest.getStartTime();
//        LocalDateTime currentEndTime = showRequest.getEndTime();
//        for(Show show : shows) {
//
//
//            if (show.getStartTime().isBefore(currentEndTime)
//                    && show.getEndTime().isAfter(currentStartTime)) {
//
//                throw new APIException(
//                        "Given show overlaps with existing show id: "
//                                + show.getShowId()
//                );
//            }
//        }
        if(showRepository.existsOverlappingShow(showRequest.getScreenId(),
                showRequest.getStartTime(),
                showRequest.getEndTime())) {
            throw new APIException("Given show overlaps with existing show id: ");
        }
        Show show = new Show();
        show.setStartTime(showRequest.getStartTime());
        show.setEndTime(showRequest.getEndTime());
        show.setBasePrice(showRequest.getBasePrice());
        show.setStatus(showRequest.getStatus());
        show.setScreen(screen);
        show.setMovie(movie);
        Show savedShow = showRepository.save(show);
        return modelMapper.map(savedShow, ShowResponse.class);

    }

    @Transactional
    @Override
    public ShowResponse updateShow(UUID showId, ShowRequest showRequest) {
        // Validate time range
        if (!showRequest.getStartTime().isBefore(showRequest.getEndTime())) {
            throw new APIException("Start time must be before end time");
        }
        Show savedShow = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show",
                                "ShowId",
                                showId
                        ));
        Screen screen = screenRepository.findById(showRequest.getScreenId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Screen",
                                "ScreenId",
                                showRequest.getScreenId()
                        ));
        LocalDateTime currentStartTime = showRequest.getStartTime();
        LocalDateTime currentEndTime = showRequest.getEndTime();
        // Check for overlapping shows on the new screen
        for(Show show : screen.getShows()) {
            // Ignore the show being updated
            if(show.getShowId().equals(showId)) {
                continue;
            }
            if (show.getStartTime().isBefore(currentEndTime)
                    && show.getEndTime().isAfter(currentStartTime)) {

                throw new APIException(
                        "Given show overlaps with existing show id: "
                                + show.getShowId()
                );
            }
        }
        Movie movie = movieRepository.findById(showRequest.getMovieId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Movie",
                                "MovieId",
                                showRequest.getMovieId()));

        savedShow.setMovie(movie);
        savedShow.setScreen(screen);
        savedShow.setBasePrice(showRequest.getBasePrice());
        savedShow.setStartTime(showRequest.getStartTime());
        savedShow.setEndTime(showRequest.getEndTime());
        savedShow.setStatus(showRequest.getStatus());
        return modelMapper.map(savedShow, ShowResponse.class);
    }

    @Transactional
    @Override
    public ShowResponse deleteShow(UUID showId) {
        Show savedShow = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show",
                                "ShowId",
                                showId
                        ));

        savedShow.getScreen().getShows().remove(savedShow);
//        showRepository.delete(savedShow); instead of deleting physically we can turn status as cancelled
        savedShow.setStatus(ShowStatus.CANCELLED);
        return modelMapper.map(savedShow, ShowResponse.class);
    }

    @Transactional
    @Override
    public ShowResponse updateShowStatus(UUID showId, ShowStatus status) {
        Show savedShow = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show",
                                "ShowId",
                                showId
                        ));
        savedShow.setStatus(status);
        return modelMapper.map(savedShow, ShowResponse.class);
    }

    @Override
    public List<ShowResponse> getAllShows() {
        List<Show> shows = showRepository.findAll();
        return shows.stream()
                .map((show) ->{
                   return modelMapper.map(show, ShowResponse.class);
                }).toList();
    }

    @Override
    public ShowResponse getShowDetails(UUID showId) {
        Show savedShow = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show",
                                "ShowId",
                                showId
                        ));
        return modelMapper.map(savedShow, ShowResponse.class);
    }

    @Override
    public List<ShowResponse> getShowsForMovie(UUID movieId) {
        List<Show> shows = showRepository.findShowsByMovieId(movieId);
        return shows.stream()
                .map((show) ->{
                    return modelMapper.map(show, ShowResponse.class);
                }).toList();
    }

    @Override
    public List<ShowResponse> getShowsInTheatre(UUID theatreId) {
        List<Show> shows = showRepository.findShowsByTheatreId(theatreId);
        return shows.stream()
                .map((show) ->{
                    return modelMapper.map(show, ShowResponse.class);
                }).toList();
    }


}
