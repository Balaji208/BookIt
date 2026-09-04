package com.bookit.backend.service;

import com.bookit.backend.payload.auth.LoginRequest;
import com.bookit.backend.payload.auth.LoginResponse;
import com.bookit.backend.payload.auth.RegisterRequest;

public interface AuthService {
    String registerUser(RegisterRequest request);
    LoginResponse loginUser(LoginRequest loginRequest);
}
