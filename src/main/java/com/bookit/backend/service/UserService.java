package com.bookit.backend.service;

import com.bookit.backend.model.User;
import com.bookit.backend.payload.user.UserResponse;
import com.bookit.backend.payload.user.UpdatePasswordRequest;
import com.bookit.backend.payload.user.UpdateProfileRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    UserResponse getUserProfileDetails();

    UserResponse updateUserDetails(UpdateProfileRequest request);

    String updateUserPassword(UpdatePasswordRequest password);

    List<UserResponse> getAllUsers(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    );

    UserResponse getUserDetails(UUID userId);

    String updateUserStatus(UUID userId, boolean status);

    User getCurrentUser();
}
