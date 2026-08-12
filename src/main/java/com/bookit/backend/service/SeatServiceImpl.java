package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Screen;
import com.bookit.backend.model.Seat;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.SeatRequest;
import com.bookit.backend.payload.SeatResponse;
import com.bookit.backend.repository.ScreenRepository;
import com.bookit.backend.repository.SeatRepository;
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
public class SeatServiceImpl implements SeatService{
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ScreenRepository screenRepository;


    @Transactional
    @Override
    public SeatResponse addSeat(UUID screenId, SeatRequest seatRequest) {

        log.debug("Creating seat in the screen screenId={}", screenId);
        Screen savedScreen = screenRepository.findByScreenIdAndActiveTrue(screenId)
                .orElseThrow(()-> {
                    log.warn("Screen not found or deactivated screenId={}", screenId);
                    return new ResourceNotFoundException(
                            "Screen",
                            "ScreenId",
                            screenId);
                });
        // Check duplicate seat within the screen
        if (seatRepository.existsByScreenScreenIdAndRowLabelAndSeatNumberAndActiveTrue(
                screenId,
                seatRequest.getRowLabel(),
                seatRequest.getSeatNumber())) {
            log.warn("Seat already exists");
            throw new APIException("Seat already exists!");
        }

        Seat seat = modelMapper.map(seatRequest, Seat.class);

        seat.setScreen(savedScreen);

        Seat savedSeat = seatRepository.save(seat);
        log.info("Seat created successfully seatId={}", savedSeat.getSeatId());
        return modelMapper.map(savedSeat, SeatResponse.class);
    }

    @Transactional
    @Override
    public SeatResponse updateSeat(UUID seatId, SeatRequest seatRequest) {

        log.debug("Updating seat seatId={}", seatId);

        Seat savedSeat = seatRepository.findBySeatIdAndActiveTrue(seatId)
                .orElseThrow(() ->
                {
                    log.warn("Seat not found or deactivated seatId={}", seatId);
                    return new ResourceNotFoundException(
                            "Seat",
                            "SeatId",
                            seatId
                    );
                });

        if (seatRepository.existsByScreenScreenIdAndRowLabelAndSeatNumberAndSeatIdNotAndActiveTrue(
                savedSeat.getScreen().getScreenId(),
                seatRequest.getRowLabel(),
                seatRequest.getSeatNumber(),
                seatId)) {
            log.warn("Seat already exists");
            throw new APIException("Seat already exists!");
        }
        savedSeat.setSeatNumber(seatRequest.getSeatNumber());
        savedSeat.setSeatType(seatRequest.getSeatType());
        savedSeat.setRowLabel(seatRequest.getRowLabel());

        log.info("Seat updated successfully seatId={}", savedSeat.getSeatId());
        return modelMapper.map(savedSeat, SeatResponse.class);
    }

    @Transactional
    @Override
    public SeatResponse deleteSeat(UUID seatId) {
        log.debug("Deactivating seat seatId={}", seatId);
        Seat savedSeat = seatRepository.findBySeatIdAndActiveTrue(seatId)
                .orElseThrow(() ->
                {
                    log.warn("Seat not found or deactivated seatId={}", seatId);
                    return new ResourceNotFoundException(
                            "Seat",
                            "SeatId",
                            seatId
                    );
                });
        // instead of deleting physically make it deactivate
        savedSeat.setActive(false);
        log.info("Deactivated seat seatId={} successfully", seatId);
        return modelMapper.map(savedSeat, SeatResponse.class);
    }

    @Override
    public PageResponse<SeatResponse> getAllSeatsInScreen(
            UUID screenId,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    ) {
        log.debug(
                "Fetching seats in screen screenId={} page={}, size={}, sortBy={}, sortOrder={}",
                screenId,
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );
        // Verify screen exists and is active
        screenRepository.findByScreenIdAndActiveTrue(screenId)
                .orElseThrow(() -> {
                    log.warn("Screen not found or deactivated screenId={}", screenId);
                    return new ResourceNotFoundException(
                            "Screen",
                            "ScreenId",
                            screenId
                    );
                });

        Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortAndOrder);
        Page<Seat> seatPage = seatRepository.findAllByScreenScreenIdAndActiveTrue(screenId, pageDetails);

        if(seatPage.isEmpty()) {
            log.warn("No seat found!");
            throw new APIException("No seats are there!");
        }
        List<SeatResponse> seatResponses = seatPage
                .getContent()
                .stream()
                .map((seat) -> modelMapper.map(seat, SeatResponse.class))
                .toList();
        PageResponse<SeatResponse> seatPageResponse =
                new PageResponse<>();

        seatPageResponse.setContent(seatResponses);
        seatPageResponse.setPageNumber(seatPage.getNumber());
        seatPageResponse.setPageSize(seatPage.getSize());
        seatPageResponse.setTotalPages(seatPage.getTotalPages());
        seatPageResponse.setTotalElements(seatPage.getTotalElements());

        log.info(
                "Seats fetched successfully: screenId={}, page={}, size={}, totalElements={}",
                screenId,
                seatPage.getNumber(),
                seatPage.getSize(),
                seatPage.getTotalElements()
        );

        return seatPageResponse;

    }

    @Override
    public SeatResponse getSeatDetails(UUID seatId) {
        log.debug("Fetching seat details seatId={}", seatId);

        Seat savedSeat = seatRepository.findBySeatIdAndActiveTrue(seatId)
                .orElseThrow(() ->
                {
                    log.warn("Seat not found or deactivated seatId={}", seatId);
                    return new ResourceNotFoundException(
                            "Seat",
                            "SeatId",
                            seatId
                    );
                });

        log.debug("Seat details fetched successfully seatId={}", seatId);

        return modelMapper.map(savedSeat, SeatResponse.class);
    }
}
