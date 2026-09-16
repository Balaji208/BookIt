package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.*;
import com.bookit.backend.payload.booking.BookingCreateRequest;
import com.bookit.backend.payload.booking.BookingResponse;
import com.bookit.backend.payload.booking.BookingSeatResponse;
import com.bookit.backend.repository.BookingRepository;
import com.bookit.backend.repository.BookingSeatRepository;
import com.bookit.backend.repository.SeatRepository;
import com.bookit.backend.repository.ShowRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowRepository showRepository;
    private final UserService userService;
    private final SeatRepository seatRepository;

    @Transactional
    @Override
    public BookingResponse createBooking(BookingCreateRequest request) {

        // 1. Validate Request
        validateRequest(request);

        UUID showId = request.getShowId();
        List<UUID> seatIds = request.getSeatIds();

        // 2. Check for valid show
        Show show = getBookableShow(showId);

        // 3. Get Current User
        User user = userService.getCurrentUser();

        // 4. Get Booked seat ids for requested show
        Set<UUID> bookedSeatIds = getBookedSeatIds(showId);

        // 5. Validate the requested seats vs booked seats for availability
        List<Seat> seats = getAndValidateSeats(
                seatIds,
                show,
                bookedSeatIds
        );

        // 6. Calculate the seat prices based on seat type
        List<BigDecimal> seatPrices =
                calculateSeatPrices(seats, show.getBasePrice());

        // 7. Calculate total amount
        BigDecimal totalAmount =
                calculateTotalAmount(seatPrices);

        // 8. Create Booking object
        Booking booking =
                createAndSaveBooking(
                        user,
                        show,
                        totalAmount
                );

        // 9. Create BookingSeat object after saving Booking obj ( because BookingSeat has booking_id as FK)
        List<BookingSeat> bookingSeats =
                createBookingSeats(
                        booking,
                        seats,
                        seatPrices
                );

        bookingSeatRepository.saveAll(bookingSeats);

        // 10. Build and return responses
        return buildBookingResponse(
                booking,
                bookingSeats
        );
    }


    private void validateRequest(BookingCreateRequest request) {

        if (request == null) {
            throw new APIException(
                    "Booking request cannot be null"
            );
        }

        if (request.getShowId() == null) {
            throw new APIException(
                    "Show ID cannot be null"
            );
        }

        if (request.getSeatIds() == null ||
                request.getSeatIds().isEmpty()) {

            throw new APIException(
                    "At least one seat must be selected"
            );
        }

        List<UUID> seatIds = request.getSeatIds();

        if (seatIds.contains(null)) {
            throw new APIException(
                    "Seat ID cannot be null"
            );
        }

        if (seatIds.size() !=
                new HashSet<>(seatIds).size()) {

            throw new APIException(
                    "Duplicate seat IDs are not allowed"
            );
        }
    }


    private Show getBookableShow(UUID showId) {

        return showRepository
                .findByShowIdAndStatus(
                        showId,
                        ShowStatus.SCHEDULED
                )
                .orElseThrow(() -> {

                    log.debug(
                            "Scheduled show with id {} not found",
                            showId
                    );

                    return new ResourceNotFoundException(
                            "Show",
                            "ShowId",
                            showId
                    );
                });
    }


    private Set<UUID> getBookedSeatIds(UUID showId) {

        return new HashSet<>(
                bookingSeatRepository.fetchBookedSeatIds(showId)
        );
    }


    private List<Seat> getAndValidateSeats(
            List<UUID> seatIds,
            Show show,
            Set<UUID> bookedSeatIds
    ) {

        List<Seat> seats =
                seatRepository.findAllBySeatIdInAndActiveTrue(
                        seatIds
                );

        validateAllSeatsExist(seatIds, seats);

        UUID showScreenId =
                show.getScreen().getScreenId();

        for (Seat seat : seats) {

            UUID seatId = seat.getSeatId();

            validateSeatAvailability(
                    seatId,
                    bookedSeatIds
            );

            validateSeatBelongsToShow(
                    seat,
                    showScreenId
            );
        }

        return seats;
    }


    private void validateAllSeatsExist(
            List<UUID> requestedSeatIds,
            List<Seat> foundSeats
    ) {

        Set<UUID> foundSeatIds =
                foundSeats.stream()
                        .map((Seat::getSeatId))
                        .collect(Collectors.toSet());

        for (UUID seatId : requestedSeatIds) {

            if (!foundSeatIds.contains(seatId)) {

                log.debug(
                        "Active seat with id {} not found",
                        seatId
                );

                throw new ResourceNotFoundException(
                        "Seat",
                        "SeatId",
                        seatId
                );
            }
        }
    }


    private void validateSeatAvailability(
            UUID seatId,
            Set<UUID> bookedSeatIds
    ) {

        if (bookedSeatIds.contains(seatId)) {

            log.debug(
                    "Seat {} is already booked",
                    seatId
            );

            throw new APIException(
                    "Seat with id : " +
                            seatId +
                            " is already booked"
            );
        }
    }


    private void validateSeatBelongsToShow(
            Seat seat,
            UUID showScreenId
    ) {

        UUID seatScreenId =
                seat.getScreen().getScreenId();

        if (!showScreenId.equals(seatScreenId)) {

            log.debug(
                    "Seat {} belongs to screen {}, " +
                            "but show belongs to screen {}",
                    seat.getSeatId(),
                    seatScreenId,
                    showScreenId
            );

            throw new APIException(
                    "Seat with id : " +
                            seat.getSeatId() +
                            " does not belong to this show"
            );
        }
    }


    private List<BigDecimal> calculateSeatPrices(
            List<Seat> seats,
            BigDecimal basePrice
    ) {

        return seats.stream()
                .map(seat ->
                        computePrice(
                                seat.getSeatType(),
                                basePrice
                        )
                )
                .toList();
    }


    private BigDecimal calculateTotalAmount(
            List<BigDecimal> seatPrices
    ) {

        return seatPrices.stream()
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }


    private Booking createAndSaveBooking(
            User user,
            Show show,
            BigDecimal totalAmount
    ) {

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setShow(show);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(totalAmount);

        return bookingRepository.save(booking);
    }


    private List<BookingSeat> createBookingSeats(
            Booking booking,
            List<Seat> seats,
            List<BigDecimal> seatPrices
    ) {

        List<BookingSeat> bookingSeats =
                new ArrayList<>();

        for (int i = 0; i < seats.size(); i++) {

            BookingSeat bookingSeat =
                    new BookingSeat();

            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seats.get(i));
            bookingSeat.setPrice(seatPrices.get(i));

            bookingSeats.add(bookingSeat);
        }

        return bookingSeats;
    }


    private BookingResponse buildBookingResponse(
            Booking booking,
            List<BookingSeat> bookingSeats
    ) {

        BookingResponse response =
                new BookingResponse();

        response.setBookingId(
                booking.getBookingId()
        );

        response.setShowId(
                booking.getShow().getShowId()
        );

        response.setStatus(
                booking.getStatus()
        );

        response.setTotalAmount(
                booking.getTotalAmount()
        );

        response.setCreatedAt(
                booking.getCreatedAt()
        );

        List<BookingSeatResponse> seatResponses =
                bookingSeats.stream()
                        .map(this::mapToBookingSeatResponse)
                        .toList();

        response.setSeats(seatResponses);

        return response;
    }


    private BookingSeatResponse mapToBookingSeatResponse(
            BookingSeat bookingSeat
    ) {

        Seat seat = bookingSeat.getSeat();

        BookingSeatResponse response =
                new BookingSeatResponse();

        response.setSeatId(
                seat.getSeatId()
        );

        response.setRowLabel(
                seat.getRowLabel()
        );

        response.setSeatNumber(
                seat.getSeatNumber()
        );

        response.setSeatType(
                seat.getSeatType()
        );

        response.setPrice(
                bookingSeat.getPrice()
        );

        return response;
    }
    private BigDecimal computePrice(SeatType seatType, BigDecimal basePrice) {
        if (basePrice == null) {
            throw new APIException("Base price cannot be null");
        }

        // Determine the percentage premium based on the seat type
        BigDecimal multiplier = switch (seatType) {
            case PREMIUM  -> new BigDecimal("1.3");   // basePrice + 30%
            case VIP      -> new BigDecimal("1.5");   // basePrice + 50%
            case RECLINER -> new BigDecimal("1.1");   // basePrice + 10%
            case REGULAR  -> BigDecimal.ONE;          // 1.0 (No change)
            case null     -> throw new APIException("Seat type cannot be null");
            default       -> throw new APIException("Invalid Seat type: " + seatType);
        };

        // Calculate final price: basePrice * multiplier
        return basePrice.multiply(multiplier);
    }

}
