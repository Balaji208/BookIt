package com.bookit.backend.repository;

import com.bookit.backend.model.Theatre;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface TheatreRepository extends JpaRepository<Theatre, UUID> {

    Optional<Theatre> findByTheatreIdAndActiveTrue(UUID theatreId);

    Page<Theatre> findAllByActiveTrue(Pageable pageable);

    boolean existsByNameAndAddressAndCityAndActiveTrue(
            String name,
            String address,
            String city
    );

    boolean existsByNameAndAddressAndCityAndActiveTrueAndTheatreIdNot(
            String name,
            String address,
            String city,
            UUID theatreId
    );
}