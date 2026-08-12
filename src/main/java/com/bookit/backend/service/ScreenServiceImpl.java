package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Screen;
import com.bookit.backend.model.Theatre;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.ScreenRequest;
import com.bookit.backend.payload.ScreenResponse;
import com.bookit.backend.repository.ScreenRepository;
import com.bookit.backend.repository.TheatreRepository;
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

@Slf4j
@Service
public class ScreenServiceImpl implements ScreenService{

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public ScreenResponse addScreen(UUID theatreId, ScreenRequest screenRequest) {

        log.debug("Creating screen name={}", screenRequest.getName());

        Theatre theatre = theatreRepository.findByTheatreIdAndActiveTrue(theatreId)
                .orElseThrow(()-> {
                    log.warn("Theatre not found theatreId={}", theatreId);
                    return new ResourceNotFoundException("Theatre", "TheatreId", theatreId);
                });

        // check if screen exists
        if (screenRepository.existsByTheatreTheatreIdAndNameAndActiveTrue(theatreId, screenRequest.getName())) {
            log.warn(
                    "Screen already exists theatreId={}, name={}",
                    theatreId,
                    screenRequest.getName()
            );
            throw new APIException("Screen already exists in this theatre");
        }
        Screen screen = modelMapper.map(screenRequest, Screen.class);
        screen.setTheatre(theatre);
        theatre.getScreens().add(screen);

        Screen savedScreen = screenRepository.save(screen);

        log.info("Screen created successfully screenId={}", savedScreen.getScreenId());
        return modelMapper.map(savedScreen, ScreenResponse.class);
    }


    @Transactional
    @Override
    public ScreenResponse updateScreen(
            UUID screenId,
            ScreenRequest screenRequest) {

        log.debug("Updating screen screenId={}", screenId);

        Screen savedScreen = screenRepository
                .findByScreenIdAndActiveTrue(screenId)
                .orElseThrow(() -> {
                    log.warn("Screen not found or deactivated screenId={}", screenId);
                    return new ResourceNotFoundException(
                            "Screen",
                            "ScreenId",
                            screenId
                    );
                });

        UUID theatreId = savedScreen.getTheatre().getTheatreId();

        // Check whether another screen in the same theatre
        // already has the requested name
        if (screenRepository.existsByTheatreTheatreIdAndNameAndScreenIdNotAndActiveTrue(
                theatreId,
                screenRequest.getName(),
                screenId
        )) {

            log.warn(
                    "Screen already exists theatreId={}, name={}",
                    theatreId,
                    screenRequest.getName()
            );

            throw new APIException(
                    "Screen already exists in this theatre"
            );
        }

        savedScreen.setName(screenRequest.getName());
        savedScreen.setTotalSeats(screenRequest.getTotalSeats());
        savedScreen.setScreenType(screenRequest.getScreenType());

        log.info("Screen updated successfully screenId={}", screenId);

        return modelMapper.map(savedScreen, ScreenResponse.class);
    }
    @Transactional
    @Override
    public ScreenResponse deleteScreen(UUID screenId) {

        log.debug("Deactivating screen screenId={}", screenId);

        Screen savedScreen = screenRepository.findByScreenIdAndActiveTrue(screenId)
                .orElseThrow(()-> {
                    log.warn("Screen not found or deactivated screenId={}", screenId);
                    return new ResourceNotFoundException("Screen", "ScreenId", screenId);
                });

        savedScreen.setActive(false);

        log.info("Screen deactivated successfully screenId={}", screenId);
        return modelMapper.map(savedScreen, ScreenResponse.class);
    }

    @Override
    public PageResponse<ScreenResponse> getAllScreens(
            UUID theatreId,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder) {

        log.debug(
                "Fetching screens page={}, size={}, sortBy={}, sortOrder={}",
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );

        // Check theatre exists
        theatreRepository.findByTheatreIdAndActiveTrue(theatreId)
                .orElseThrow(() ->
                {
                    log.warn("Theatre not found  or deactivated theatreId={}", theatreId);
                    return new ResourceNotFoundException(
                            "Theatre",
                            "TheatreId",
                            theatreId
                    );
                });

        // Sort
        Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Pagination
        Pageable pageDetails =
                PageRequest.of(pageNumber, pageSize, sortAndOrder);

        // Get paginated screens belonging to theatre
        Page<Screen> screensPage =
                screenRepository.findAllByTheatreTheatreIdAndActiveTrue(
                        theatreId,
                        pageDetails
                );

        if (screensPage.isEmpty()) {
            log.warn("No screen found theatreId={}", theatreId);
            throw new APIException("No screens found for this theatre");
        }

        // Convert Screen → ScreenResponse
        List<ScreenResponse> screenResponses =
                screensPage.stream()
                        .map(screen ->
                                modelMapper.map(
                                        screen,
                                        ScreenResponse.class
                                ))
                        .toList();

        // Create pagination response
        PageResponse<ScreenResponse> pageResponse =
                new PageResponse<>();

        pageResponse.setContent(screenResponses);
        pageResponse.setPageNumber(screensPage.getNumber());
        pageResponse.setPageSize(screensPage.getSize());
        pageResponse.setTotalPages(screensPage.getTotalPages());
        pageResponse.setTotalElements(screensPage.getTotalElements());

        log.info(
                "Screens fetched successfully theatreId={}, page={}, returned={}, totalElements={}",
                theatreId,
                screensPage.getNumber(),
                screensPage.getNumberOfElements(),
                screensPage.getTotalElements()
        );

        return pageResponse;
    }
    @Override
    public ScreenResponse getScreenDetails(UUID screenId) {

        log.debug("Fetching screen details screenId={}", screenId);

        Screen savedScreen = screenRepository.findByScreenIdAndActiveTrue(screenId)
                .orElseThrow(()-> {
                    log.warn("Screen not found  or deactivated screenId={}", screenId);
                    return new ResourceNotFoundException("Screen", "ScreenId", screenId);
                });

        log.debug("Screen details fetched successfully screenId={}", screenId);

        return modelMapper.map(savedScreen, ScreenResponse.class);
    }
}
