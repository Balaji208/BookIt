package com.bookit.backend.repository;

import com.bookit.backend.model.Screen;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, UUID> {

    Page<Screen> findAllByTheatreTheatreIdAndActiveTrue(
            UUID theatreId,
            Pageable pageDetails
    );

    Optional<Screen> findByScreenIdAndActiveTrue(UUID screenId);

    boolean existsByTheatreTheatreIdAndNameAndActiveTrue(
            UUID theatreId,
            String name
    );

    boolean existsByTheatreTheatreIdAndNameAndScreenIdNotAndActiveTrue(
            UUID theatreId,
            String name,
            UUID screenId
    );
}