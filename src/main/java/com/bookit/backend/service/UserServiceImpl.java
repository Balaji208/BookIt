package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.User;
import com.bookit.backend.payload.user.UserResponse;
import com.bookit.backend.payload.user.UpdatePasswordRequest;
import com.bookit.backend.payload.user.UpdateProfileRequest;
import com.bookit.backend.repository.UserRepository;
import com.bookit.backend.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserResponse getUserProfileDetails() {
        User user = getCurrentUser();
        log.info("User details fetched successfully for id : {}", user.getUserId() );
        return modelMapper.map(user, UserResponse.class);
    }

    @Transactional
    @Override
    public UserResponse updateUserDetails(UpdateProfileRequest request) {

        User user = getCurrentUser();
        log.info("Updating User details for id : {}", user.getUserId());
        if(userRepository.existsByEmailAndUserIdNotAndActiveTrue(
                request.getEmail(),
                user.getUserId())) {
            throw new APIException("Email already exists");
        }
        user.setEmail(request.getEmail());
        user.setName(request.getName());

        userRepository.save(user);

        log.info("User details updated successfully :{}", user.getUserId());
        return modelMapper.map(user, UserResponse.class);

    }

    @Transactional
    @Override
    public String updateUserPassword(UpdatePasswordRequest request) {

        User user = getCurrentUser();
        log.info("Updating user password for user : {}", user.getUserId());
        if(!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            throw new APIException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder
                .encode(request.getNewPassword())
        );
        userRepository.save(user);

        return "Password updated successfully!";
    }

    @Override
    public List<UserResponse> getAllUsers(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder
    ) {
        log.debug(
                "Fetching Users page={}, size={}, sortBy={}, sortOrder={}",
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );

        Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortAndOrder);
        Page<User> userPage = userRepository.findAllByActiveTrue(pageDetails);

        if(userPage.isEmpty()) {
            log.warn("No users found!");
        }
        List<UserResponse> userResponses = userPage
                .stream()
                .map((user) -> modelMapper.map(user, UserResponse.class)
                ).toList();
        log.info(
                "Users fetched successfully: page={}, size={}, totalElements={}",
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements()
        );
        return userResponses;
    }

    @Override
    public UserResponse getUserDetails(UUID userId) {
        User user = userRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User",
                        "UserId",
                        userId
                ));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public String updateUserStatus(UUID userId, boolean status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User",
                        "UserId",
                        userId
                ));

        user.setActive(status);

        return "Status changed successfully";
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new APIException("User is not authenticated");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUser();
    }
}
