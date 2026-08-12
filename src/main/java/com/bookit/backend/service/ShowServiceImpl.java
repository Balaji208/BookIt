package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Movie;
import com.bookit.backend.model.Screen;
import com.bookit.backend.model.Show;
import com.bookit.backend.model.ShowStatus;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.ShowRequest;
import com.bookit.backend.payload.ShowResponse;
import com.bookit.backend.repository.MovieRepository;
import com.bookit.backend.repository.ScreenRepository;
import com.bookit.backend.repository.ShowRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
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
            log.warn("Start time must be before end time startTime={}, endTime={}",
                    showRequest.getStartTime(),
                    showRequest.getEndTime()
            );
            throw new APIException("Start time must be before end time");
        }

        Screen savedScreen = screenRepository.findByScreenIdAndActiveTrue(showRequest.getScreenId())
                .orElseThrow(()-> {
                    log.warn("Screen not found or deactivated screenId={}", showRequest.getScreenId());
                    return new ResourceNotFoundException(
                            "Screen",
                            "ScreenId",
                            showRequest.getScreenId());
                });

        Movie savedMovie = movieRepository.findByMovieIdAndActiveTrue(showRequest.getMovieId())
                .orElseThrow(() -> {
                    log.warn("Movie not found or deactivated movieId={}", showRequest.getMovieId());
                    return new ResourceNotFoundException(
                            "Movie",
                            "MovieId",
                            showRequest.getMovieId());
                });

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
            log.warn("Given show overlaps with existing show");
            throw new APIException("Given show overlaps with an existing show");
        }

        Show show = new Show();
        show.setStartTime(showRequest.getStartTime());
        show.setEndTime(showRequest.getEndTime());
        show.setBasePrice(showRequest.getBasePrice());
        show.setStatus(ShowStatus.SCHEDULED);
        show.setScreen(savedScreen);
        show.setMovie(savedMovie);
        Show savedShow = showRepository.save(show);

        log.info("Show created successfully showId={}", savedShow.getShowId());
        return modelMapper.map(savedShow, ShowResponse.class);

    }

    @Transactional
    @Override
    public ShowResponse updateShow(UUID showId, ShowRequest showRequest) {

        log.debug("Updating show showId={}", showId);

        // Validate time range
        if (!showRequest.getStartTime().isBefore(showRequest.getEndTime())) {
            log.warn(
                    "Invalid show time range startTime={}, endTime={}",
                    showRequest.getStartTime(),
                    showRequest.getEndTime()
            );

            throw new APIException("Start time must be before end time");
        }

        // Find active/non-cancelled show
        Show savedShow = showRepository
                .findByShowIdAndStatusNot(showId, ShowStatus.CANCELLED)
                .orElseThrow(() -> {
                    log.warn("Show not found or already cancelled showId={}", showId);

                    return new ResourceNotFoundException(
                            "Show",
                            "ShowId",
                            showId
                    );
                });

        // Validate new screen
        Screen savedScreen = screenRepository
                .findByScreenIdAndActiveTrue(showRequest.getScreenId())
                .orElseThrow(() -> {
                    log.warn(
                            "Screen not found or deactivated screenId={}",
                            showRequest.getScreenId()
                    );

                    return new ResourceNotFoundException(
                            "Screen",
                            "ScreenId",
                            showRequest.getScreenId()
                    );
                });

        // Check overlap on new screen
        if (showRepository.existsOverlappingShowForUpdate(
                showRequest.getScreenId(),
                showId,
                showRequest.getStartTime(),
                showRequest.getEndTime())) {

            log.warn(
                    "Show overlaps with existing show showId={}, screenId={}",
                    showId,
                    showRequest.getScreenId()
            );

            throw new APIException(
                    "Given show overlaps with an existing show"
            );
        }

        // Validate movie
        Movie savedMovie = movieRepository
                .findByMovieIdAndActiveTrue(showRequest.getMovieId())
                .orElseThrow(() -> {
                    log.warn(
                            "Movie not found or deactivated movieId={}",
                            showRequest.getMovieId()
                    );

                    return new ResourceNotFoundException(
                            "Movie",
                            "MovieId",
                            showRequest.getMovieId()
                    );
                });

        // Update fields
        savedShow.setMovie(savedMovie);
        savedShow.setScreen(savedScreen);
        savedShow.setBasePrice(showRequest.getBasePrice());
        savedShow.setStartTime(showRequest.getStartTime());
        savedShow.setEndTime(showRequest.getEndTime());

        log.info("Show updated successfully showId={}", showId);

        return modelMapper.map(savedShow, ShowResponse.class);
    }
    @Transactional
    @Override
    public ShowResponse deleteShow(UUID showId) {
        log.debug("Cancelling show showId={}", showId);

        Show savedShow = showRepository.findByShowIdAndStatusNot(showId, ShowStatus.CANCELLED)
                .orElseThrow(() -> {
                    log.warn("Show not found or already cancelled showId={}", showId);
                    return new ResourceNotFoundException(
                            "Show",
                            "ShowId",
                            showId
                    );
                });

      //  savedShow.getScreen().getShows().remove(savedShow);
//        showRepository.delete(savedShow); instead of deleting physically we can turn status as cancelled
        savedShow.setStatus(ShowStatus.CANCELLED);
        log.info("Show cancelled showId={}", savedShow.getShowId());
        return modelMapper.map(savedShow, ShowResponse.class);
    }

    @Transactional
    @Override
    public ShowResponse updateShowStatus(UUID showId, ShowStatus status) {
        log.debug("Updating show status showId={}", showId);

        Show savedShow = showRepository.findByShowIdAndStatusNot(showId, ShowStatus.CANCELLED)
                .orElseThrow(() -> {
                    log.warn("Show not found or already cancelled showId={}", showId);
                    return new ResourceNotFoundException(
                            "Show",
                            "ShowId",
                            showId
                    );
                });

        ShowStatus oldStatus = savedShow.getStatus();

        savedShow.setStatus(status);

        log.info(
                "Show status updated showId={}, oldStatus={}, newStatus={}",
                showId,
                oldStatus,
                status
        );
        return modelMapper.map(savedShow, ShowResponse.class);
    }

    @Override
    public PageResponse<ShowResponse> getAllShows(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    ) {
        log.debug(
                "Fetching shows page={}, size={}, sortBy={}, sortOrder={}",
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );

        Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortAndOrder);
        Page<Show> showPage = showRepository.findAllByStatusNot(pageDetails, ShowStatus.CANCELLED);

        if(showPage.isEmpty()) {
            log.warn("No show found!");
            throw new APIException("No shows are there!");
        }

        List<ShowResponse> showResponses = showPage
                .getContent()
                .stream()
                .map((show) -> modelMapper.map(show, ShowResponse.class))
                .toList();

        PageResponse<ShowResponse> showPageResponse =
                new PageResponse<>();
        showPageResponse.setContent(showResponses);
        showPageResponse.setPageNumber(showPage.getNumber());
        showPageResponse.setPageSize(showPage.getSize());
        showPageResponse.setTotalPages(showPage.getTotalPages());
        showPageResponse.setTotalElements(showPage.getTotalElements());

        log.info(
                "Shows fetched successfully: page={}, size={}, totalElements={}",
                showPage.getNumber(),
                showPage.getSize(),
                showPage.getTotalElements()
        );

        return showPageResponse;
    }

    @Override
    public ShowResponse getShowDetails(UUID showId) {
        log.debug("Fetching show details showId={}", showId);

        Show savedShow = showRepository.findByShowIdAndStatusNot(showId, ShowStatus.CANCELLED)
                .orElseThrow(() -> {
                    log.warn("Show not found or already cancelled showId={}", showId);
                    return new ResourceNotFoundException(
                            "Show",
                            "ShowId",
                            showId
                    );
                });
        log.info("Show details fetched showId={}", showId);
        return modelMapper.map(savedShow, ShowResponse.class);
    }

    @Override
    public List<ShowResponse> getShowsForMovie(UUID movieId) {
        log.debug("Fetching shows for movie movieId={}", movieId);
        List<Show> shows = showRepository.findShowsByMovieIdActiveTrueAndShowStatusNot(movieId, ShowStatus.CANCELLED);

        log.info("Shows fetched for movie movieId={}", movieId);
        return shows.stream()
                .map((show) ->{
                    return modelMapper.map(show, ShowResponse.class);
                }).toList();
    }

    @Override
    public List<ShowResponse> getShowsInTheatre(UUID theatreId) {
        log.debug("Fetching shows in theatre theatreId={}", theatreId);
        List<Show> shows = showRepository.findShowsByTheatreIdActiveTrueAndShowStatusNot(theatreId, ShowStatus.CANCELLED);
        log.info("Shows fetched in theatre theatreId={}", theatreId);
        return shows.stream()
                .map((show) ->{
                    return modelMapper.map(show, ShowResponse.class);
                }).toList();
    }


}
