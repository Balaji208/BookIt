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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

        // 1. Validate the booking request
        validateRequest(request);

        UUID showId = request.getShowId();
        List<UUID> seatIds = request.getSeatIds();

        // 2. Get the show only if it is currently bookable
        Show show = getBookableShow(showId);

        // 3. Get the authenticated customer
        User user = userService.getCurrentUser();

        // 4. Validate requested seats and fetch them in one DB query
        List<Seat> seats = validateAndPrepareSeats(
                seatIds,
                show
        );

        // 5. Create Booking and BookingSeat records
        List<BookingSeat> bookingSeats = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Seat seat : seats) {

            BigDecimal seatPrice = calculateSeatPrice(
                    seat,
                    show.getBasePrice()
            );

            BookingSeat bookingSeat = new BookingSeat();

            bookingSeat.setSeat(seat);
            bookingSeat.setPrice(seatPrice);

            bookingSeats.add(bookingSeat);

            totalAmount = totalAmount.add(seatPrice);
        }

        Booking booking = createBooking(
                user,
                show,
                totalAmount
        );

        // BookingSeat contains booking_id as FK,
        // therefore Booking must exist before BookingSeat records.
        for (BookingSeat bookingSeat : bookingSeats) {
            bookingSeat.setBooking(booking);
        }

        bookingSeatRepository.saveAll(bookingSeats);

        // 6. Build the API response
        return buildBookingResponse(
                booking,
                bookingSeats
        );
    }


    /**
     * Validates basic request-level constraints.
     *
     * These checks do not require database access.
     */
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

        List<UUID> seatIds = request.getSeatIds();

        if (seatIds == null || seatIds.isEmpty()) {
            throw new APIException(
                    "At least one seat must be selected"
            );
        }

        if (seatIds.contains(null)) {
            throw new APIException(
                    "Seat ID cannot be null"
            );
        }

        // The same seat should not appear twice
        // in a single booking request.
        if (seatIds.size() != new HashSet<>(seatIds).size()) {
            throw new APIException(
                    "Duplicate seat IDs are not allowed"
            );
        }
    }


    /**
     * Retrieves a show only when it is in SCHEDULED state.
     *
     * CANCELLED and COMPLETED shows cannot be booked.
     */
    private Show getBookableShow(UUID showId) {

        return showRepository
                .findByShowIdAndStatus(
                        showId,
                        ShowStatus.SCHEDULED
                )
                .orElseThrow(() -> {

                    log.debug(
                            "Bookable show with id {} not found",
                            showId
                    );

                    return new ResourceNotFoundException(
                            "Show",
                            "ShowId",
                            showId
                    );
                });
    }


    /**
     * Fetches all requested seats in a single database query
     * and validates their existence, availability and screen.
     *
     * This avoids querying the database once for every seat.
     */
    private List<Seat> validateAndPrepareSeats(
            List<UUID> seatIds,
            Show show
    ) {

        // Fetch all active requested seats in one query.
        List<Seat> seats =
                seatRepository.findAllBySeatIdInAndActiveTrue(
                        seatIds
                );

        // Batch queries can return fewer records than requested.
        // Therefore explicitly verify that every requested seat exists.
        validateSeatExistence(seatIds, seats);

        // Fetch already booked seats for this show.
        // Only seats from CONFIRMED bookings are considered occupied.
        Set<UUID> bookedSeatIds =
                new HashSet<>(
                        bookingSeatRepository.fetchBookedSeatIds(
                                show.getShowId()
                        )
                );

        UUID showScreenId =
                show.getScreen().getScreenId();

        for (Seat seat : seats) {

            UUID seatId = seat.getSeatId();

            // A cancelled booking does not occupy the seat.
            if (bookedSeatIds.contains(seatId)) {

                log.debug(
                        "Seat {} is already booked for show {}",
                        seatId,
                        show.getShowId()
                );

                throw new APIException(
                        "Seat with id : " +
                                seatId +
                                " is already booked"
                );
            }

            // A seat belongs to a physical screen.
            // A show also belongs to a screen.
            // Therefore the selected seat must belong
            // to the show's screen.
            UUID seatScreenId =
                    seat.getScreen().getScreenId();

            if (!showScreenId.equals(seatScreenId)) {

                log.debug(
                        "Seat {} belongs to screen {}, " +
                                "but show {} belongs to screen {}",
                        seatId,
                        seatScreenId,
                        show.getShowId(),
                        showScreenId
                );

                throw new APIException(
                        "Seat with id : " +
                                seatId +
                                " does not belong to this show"
                );
            }
        }

        return seats;
    }


    /**
     * Ensures that every requested seat was actually
     * found in the database.
     *
     * findAllBySeatIdInAndActiveTrue() may return fewer
     * records than the number of requested IDs.
     */
    private void validateSeatExistence(
            List<UUID> requestedSeatIds,
            List<Seat> foundSeats
    ) {

        Set<UUID> foundSeatIds =
                foundSeats.stream()
                        .map(Seat::getSeatId)
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


    /**
     * Creates and persists the Booking entity.
     *
     * The total amount is stored as a snapshot of the
     * price paid when the booking was created.
     */
    private Booking createBooking(
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


    /**
     * Calculates the price of one seat based on its type.
     *
     * The calculated value is stored in BookingSeat so
     * that historical booking prices are preserved.
     */
    private BigDecimal calculateSeatPrice(
            Seat seat,
            BigDecimal basePrice
    ) {

        if (basePrice == null) {
            throw new APIException(
                    "Base price cannot be null"
            );
        }

        return switch (seat.getSeatType()) {

            case REGULAR ->
                    basePrice;

            case PREMIUM ->
                    basePrice.multiply(
                            new BigDecimal("1.30")
                    );

            case VIP ->
                    basePrice.multiply(
                            new BigDecimal("1.50")
                    );

            case RECLINER ->
                    basePrice.multiply(
                            new BigDecimal("1.10")
                    );

            case null ->
                    throw new APIException(
                            "Seat type cannot be null"
                    );

            default ->
                    throw new APIException(
                            "Invalid seat type: " +
                                    seat.getSeatType()
                    );
        };
    }


    /**
     * Converts the persisted booking and its seats
     * into the API response DTO.
     */
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
                        .map(this::mapBookingSeatResponse)
                        .toList();

        response.setSeats(seatResponses);

        return response;
    }


    /**
     * Maps a BookingSeat entity to its response DTO.
     */
    private BookingSeatResponse mapBookingSeatResponse(
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
}