package com.bookit.backend.controller;

import com.bookit.backend.config.AppConstants;
import com.bookit.backend.model.User;
import com.bookit.backend.payload.UserResponse;
import com.bookit.backend.payload.user.UpdatePasswordRequest;
import com.bookit.backend.payload.user.UpdateProfileRequest;
import com.bookit.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getUserProfileDetails() {
        UserResponse userResponse = userService.getUserProfileDetails();
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping("/users/me")
    public ResponseEntity<UserResponse> updateUserDetails(
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse userResponse = userService.updateUserDetails(request);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PatchMapping("/users/me/password")
    public ResponseEntity<String> updateUserPassword(
            @Valid @RequestBody UpdatePasswordRequest password) {
        String response = userService.updateUserPassword(password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(
                    name = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER
            )
            @Min(value = 0, message = "Page number cannot be negative")
            Integer pageNumber,

            @RequestParam(
                    name = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE
            )
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 50, message = "Page size cannot exceed 50")
            Integer pageSize,

            @RequestParam(
                    name = "sortBy",
                    defaultValue = "name"
            )
            String sortBy,

            @RequestParam(
                    name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR
            )
            String sortOrder
    ) {
        List<UserResponse> userResponses = userService.getAllUsers(
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserDetails(
            @PathVariable UUID userId) {
        UserResponse userResponse = userService.getUserDetails(userId);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable UUID userId,
            @RequestBody boolean status) {
        String response = userService.updateUserStatus(userId, status);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
