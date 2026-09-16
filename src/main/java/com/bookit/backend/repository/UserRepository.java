package com.bookit.backend.repository;

import com.bookit.backend.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(@NotBlank @Email String email);
    Optional<User> findByUserIdAndActiveTrue(UUID userId);

    Page<User> findAllByActiveTrue(Pageable pageable);

    boolean existsByEmailAndUserIdNotAndActiveTrue(@NotBlank @Email String email, UUID userId);

}
