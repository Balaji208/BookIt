package com.bookit.backend.repository;

import com.bookit.backend.model.Seat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {



    boolean existsByScreenScreenIdAndRowLabelAndSeatNumber(UUID screenId, @NotBlank String rowLabel, @NotNull @Positive Integer seatNumber);

    boolean existsByScreenScreenIdAndRowLabelAndSeatNumberAndSeatIdNot(
            UUID screenId,
            String rowLabel,
            Integer seatNumber,
            UUID seatId
    );

    List<Seat> findAllByScreenScreenIdAndActiveTrue(UUID screenId);
}
