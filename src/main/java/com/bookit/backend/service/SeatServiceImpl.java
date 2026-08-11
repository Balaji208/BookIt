package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Screen;
import com.bookit.backend.model.Seat;
import com.bookit.backend.payload.SeatRequest;
import com.bookit.backend.payload.SeatResponse;
import com.bookit.backend.repository.ScreenRepository;
import com.bookit.backend.repository.SeatRepository;
import com.bookit.backend.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SeatServiceImpl implements SeatService{
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Transactional
    @Override
    public SeatResponse addSeat(UUID screenId, SeatRequest seatRequest) {

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Screen",
                                "ScreenId",
                                screenId
                        ));
        System.out.println(screen);
        // Check duplicate seat within the screen
        if (seatRepository.existsByScreenScreenIdAndRowLabelAndSeatNumber(
                screenId,
                seatRequest.getRowLabel(),
                seatRequest.getSeatNumber())) {

            throw new APIException("Seat already exists!");
        }

        Seat seat = modelMapper.map(seatRequest, Seat.class);

        seat.setScreen(screen);

        Seat savedSeat = seatRepository.save(seat);

        return modelMapper.map(savedSeat, SeatResponse.class);
    }

    @Transactional
    @Override
    public SeatResponse updateSeat(UUID seatId, SeatRequest seatRequest) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat",
                                "SeatId",
                                seatId
                        ));
        if (seatRepository.existsByScreenScreenIdAndRowLabelAndSeatNumberAndSeatIdNot(
                seat.getScreen().getScreenId(),
                seatRequest.getRowLabel(),
                seatRequest.getSeatNumber(),
                seatId)) {

            throw new APIException("Seat already exists!");
        }
        seat.setSeatNumber(seatRequest.getSeatNumber());
        seat.setSeatType(seatRequest.getSeatType());
        seat.setRowLabel(seatRequest.getRowLabel());
        return modelMapper.map(seat, SeatResponse.class);
    }

    @Transactional
    @Override
    public SeatResponse deleteSeat(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat",
                                "SeatId",
                                seatId
                        ));
        // instead of deleting physically make it deactivate
        seat.setActive(false);
        return modelMapper.map(seat, SeatResponse.class);
    }

    @Override
    public List<SeatResponse> getAllSeatsInScreen(UUID screenId) {
        List<Seat> seats = seatRepository.findAllByScreenScreenIdAndActiveTrue(screenId);
        return seats.stream()
                .map((seat)-> {
                    return modelMapper.map(seat, SeatResponse.class);
                }).toList();

    }

    @Override
    public SeatResponse getSeatDetails(UUID seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat",
                                "SeatId",
                                seatId
                        ));
        return modelMapper.map(seat, SeatResponse.class);
    }
}
