package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.model.Role;
import com.bookit.backend.model.User;
import com.bookit.backend.payload.auth.LoginRequest;
import com.bookit.backend.payload.auth.LoginResponse;
import com.bookit.backend.payload.auth.RegisterRequest;
import com.bookit.backend.repository.UserRepository;
import com.bookit.backend.security.CustomUserDetails;
import com.bookit.backend.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    // 1. Inject the expiration time from application.properties / application.yml
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    public String registerUser(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new APIException("Email already registered");
        }

        log.debug("Creating User={}", request.getName());
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // We should not store raw password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // As of now register with CUSTOMER role
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        userRepository.save(user);
        log.info("User saved successfully, \nEmail : {} \nName : {}",
                user.getEmail(), user.getName());
        return "User registered successfully";
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {

        // 1. Authenticate the user credentials using Spring Security's AuthenticationManager.
        // This looks up the user, verifies the password against the encoded database password,
        // and throws an exception if the credentials are invalid.
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        // 2. Retrieve the authenticated user's details from the authentication principal.
        // The principal is the CustomUserDetails object created by CustomUserDetailsService.
        // It wraps the BookIt User entity and provides the information Spring Security needs.
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        // 3. Generate a secure JSON Web Token (JWT) using the authenticated user's context.
        String token = jwtService.generateToken(userDetails);

        // 4. Return the successfully generated token, the authorization type,
        // and the token expiration duration (e.g., 3,600,000 milliseconds = 1 hour).
        return new LoginResponse(
                token,
                "Bearer",
                jwtExpiration
        );
    }

}
