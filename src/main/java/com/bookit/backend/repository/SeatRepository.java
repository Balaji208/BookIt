package com.bookit.backend.repository;

import com.bookit.backend.model.Seat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;
@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {





    Page<Seat> findAllByScreenScreenIdAndActiveTrue(UUID screenId, Pageable pageDetails);

    boolean existsByScreenScreenIdAndRowLabelAndSeatNumberAndActiveTrue(
            UUID screenId,
            String rowLabel,
            Integer seatNumber);

    boolean existsByScreenScreenIdAndRowLabelAndSeatNumberAndSeatIdNotAndActiveTrue(
            UUID screenId,
            String rowLabel,
            Integer seatNumber,
            UUID seatId);


    Optional<Seat> findBySeatIdAndActiveTrue(UUID seatId);
}
