package com.bookit.backend.repository;

import com.bookit.backend.model.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, UUID> {

    @Query(value = """
        SELECT bs.seat_id FROM
        booking_seats bs JOIN bookings b
        ON bs.booking_id = b.booking_id
        WHERE b.show_id = :showId
    """, nativeQuery = true)
    List<UUID> fetchBookedSeatIds(UUID showId);

    List<BookingSeat> findByBookingBookingId(UUID bookingId);


    @Query("""
    SELECT bs
    FROM BookingSeat bs
    JOIN FETCH bs.seat
    WHERE bs.booking.bookingId IN :bookingIds
""")
    List<BookingSeat> findByBookingBookingIdIn(
            @Param("bookingIds") List<UUID> bookingIds
    );
}
