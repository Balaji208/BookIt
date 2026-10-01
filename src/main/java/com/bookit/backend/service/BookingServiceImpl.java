package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.*;
import com.bookit.backend.payload.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.swing.*;
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

    @Override
    public PageResponse<BookingResponse> getAllBookings(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    ) {
        // 1. Get the currently authenticated user
        User user = userService.getCurrentUser();

        UUID userId = user.getUserId();

        Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortAndOrder);

        // 2. Fetch all bookings for logged-in user
        Page<Booking> bookingPage = bookingRepository.findByUserUserId(userId, pageDetails);

        // No bookings -> return empty list
        if (bookingPage.isEmpty()) {
            return new PageResponse<BookingResponse>();
        }

        // 3. Extract all booking IDs
        List<UUID> bookingIds = bookingPage.stream()
                .map((booking) -> booking.getBookingId())
                .toList();

        // 4. Fetch BookingSeats for ALL bookings in one query
        List<BookingSeat> bookingSeats =
                bookingSeatRepository.findByBookingBookingIdIn(
                        bookingIds
                );


        /*
         * 5. Group BookingSeats by booking ID.
         *
         * Instead of querying the database for every booking:
         *
         *     booking 1 -> query
         *     booking 2 -> query
         *     booking 3 -> query
         *     ...
         *
         * we already have all BookingSeats in memory.
         */

        Map<UUID, List<BookingSeat>> bookingSeatMap =
                bookingSeats.stream()
                        .collect(Collectors.groupingBy(
                                bookingSeat ->
                                        bookingSeat.
                                                getBooking()
                                                .getBookingId()
                        ));


        // Convert bookings into response DTOs
        List<BookingResponse> bookingResponses = new ArrayList<>();

        for(Booking booking : bookingPage.getContent()) {

            UUID bookingId = booking.getBookingId();

            List<BookingSeat> bookedSeats =
                    bookingSeatMap.getOrDefault(
                            bookingId,
                            Collections.emptyList()
                    );

            if (bookedSeats.isEmpty()) {
                log.debug(
                        "Booked seats are empty for booking id : {}",
                        bookingId
                );
            }
            // 4. Convert bookingSeats into DTO response
            List<BookingSeatResponse> bookingSeatResponses =
                    bookedSeats.stream()
                            .map(this::mapBookingSeatResponse)
                            .toList();
            // 8. Convert Booking into BookingResponse
            BookingResponse bookingResponse =
                    new BookingResponse();

            bookingResponse.setBookingId(bookingId);

            bookingResponse.setShowId(
                    booking.getShow().getShowId()
            );

            bookingResponse.setStatus(
                    booking.getStatus()
            );

            bookingResponse.setTotalAmount(
                    booking.getTotalAmount()
            );

            bookingResponse.setSeats(
                    bookingSeatResponses
            );
            bookingResponse.setCreatedAt(
                    booking.getCreatedAt()
            );

            bookingResponses.add(bookingResponse);
        }

        PageResponse<BookingResponse> bookingResponsePage =
                new PageResponse<>();

        bookingResponsePage.setContent(bookingResponses);
        bookingResponsePage.setTotalPages(bookingPage.getTotalPages());
        bookingResponsePage.setPageSize(bookingPage.getSize());
        bookingResponsePage.setPageNumber(bookingPage.getNumber());
        bookingResponsePage.setTotalElements(bookingPage.getTotalElements());

        return bookingResponsePage;
    }

    @Override
    public BookingResponse getBookingDetails(UUID bookingId) {

        // 1. Validate booking ID
        if(bookingId == null) {
            throw new APIException("Booking Id can't be null to fetch details");
        }

        // 2. Get currently authenticated user
        User currentUser = userService.getCurrentUser();

        UUID currentUserId = currentUser.getUserId();

        // 3. Fetch booking
        Booking booking = bookingRepository.
                findById(bookingId)
                .orElseThrow(() -> {
                    log.debug(
                            "Booking not found for id : {}",
                            bookingId
                    );
                    return new ResourceNotFoundException(
                            "Booking",
                            bookingId.toString(),
                            "bookingId"
                    );
                });

        // 4. Verify booking belongs to current user
        if (!booking.getUser().getUserId().equals(currentUserId)) {
            log.debug(
                    "User {} attempted to access booking {}",
                    currentUserId,
                    bookingId
            );

            throw new ResourceNotFoundException(
                    "Booking",
                    bookingId.toString(),
                    "bookingId"
            );
        }

        // 5. Fetch booking seats
        List<BookingSeat> bookedSeats = bookingSeatRepository.
                findByBookingBookingId(bookingId);

        if (bookedSeats.isEmpty()) {
            log.debug(
                    "Booked seats are empty for booking id : {}",
                    bookingId
            );
        }

        // 6. Build response
        return buildBookingResponse(
                booking,
                bookedSeats
        );
    }

    @Transactional
    @Override
    public String cancelBooking(UUID bookingId) {

        // 1. Validate Booking Id
        if(bookingId == null) {
            throw new APIException("Booking Id can't be null to fetch details");
        }
        // 2. Get currently authenticated user
        User currentUser = userService.getCurrentUser();

        UUID currentUserId = currentUser.getUserId();

        log.info("1. Current user got user id : {}", currentUserId);

        // 3. Fetch booking
        Booking booking = bookingRepository.
                findById(bookingId)
                .orElseThrow(() -> {
                    log.debug(
                            "Booking not found for id : {}",
                            bookingId
                    );
                    return new ResourceNotFoundException(
                            "Booking",
                            bookingId.toString(),
                            "bookingId"
                    );
                });

        // 4. Verify booking belongs to current user
        if (!booking.getUser().getUserId().equals(currentUserId)) {
            log.debug(
                    "User {} attempted to access booking {}",
                    currentUserId,
                    bookingId
            );
            throw new ResourceNotFoundException(
                    "Booking",
                    bookingId.toString(),
                    "bookingId"
            );
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new APIException("Booking is already cancelled");
        }

        // 5. Update the status
        booking.setStatus(BookingStatus.CANCELLED);

        /*
         * No bookingRepository.save() is required here.
         *
         * Because this entity was loaded inside the @Transactional
         * persistence context, Hibernate tracks the change through
         * dirty checking and generates the UPDATE during flush/commit.
         */

        log.info(
                "Booking cancelled successfully. bookingId={}, userId={}",
                bookingId,
                currentUserId
        );

        return "Booking cancelled successfully!";
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
        response.setCreatedAt(booking.getCreatedAt());
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