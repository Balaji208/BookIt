package com.bookit.backend.repository;

import com.bookit.backend.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("""
    SELECT b
    FROM Booking b
    JOIN FETCH b.show
    WHERE b.user.userId = :userId
""")
    Page<Booking> findByUserUserId(
            UUID userId,
            Pageable pageDetails);
}
