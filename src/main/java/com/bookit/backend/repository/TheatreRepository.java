package com.bookit.backend.repository;

import com.bookit.backend.model.Theatre;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, UUID> {

    boolean existsByNameAndAddressAndCity(@NotNull String name, @NotNull String address, @NotNull String city);
}
