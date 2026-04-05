package com.cafe.service;

import com.cafe.dto.request.LoginRequest;
import com.cafe.dto.request.RegisterRequest;
import com.cafe.dto.response.AuthResponse;
import com.cafe.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser(String email);
}
