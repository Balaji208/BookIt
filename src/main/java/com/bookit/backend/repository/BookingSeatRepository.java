package com.bookit.backend.repository;

import com.bookit.backend.model.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, UUID> {
    @Query(value = """
    SELECT EXISTS (
        SELECT 1 FROM booking_seats bs 
        JOIN booking b ON bs.booking_id = b.booking_id
        WHERE b.show_id = ?1 AND bs.seat_id = ?2
    )
    """, nativeQuery = true)
    boolean existsByShowIdAndSeatId(UUID showId, UUID seatId);

    @Query(value = """
        SELECT bs.seatId FROM
        booking_seats bs JOIN bookings b
        ON bs.booking_id = b.booking_id
        WHERE b.show_id = :showId
    """, nativeQuery = true)
    List<UUID> fetchBookedSeatIds(UUID showId);
}
