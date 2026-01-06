package com.example.crudjob.service;

import com.example.crudjob.dto.request.LoginRequestDTO;
import com.example.crudjob.dto.request.RegisterRequestDTO;
import com.example.crudjob.dto.response.AuthResponseDTO;

public interface IAuthService {

    /**
     * Đăng nhập user
     */
    AuthResponseDTO login(LoginRequestDTO request);

    /**
     * Đăng ký user mới
     */
    AuthResponseDTO register(RegisterRequestDTO request);

    /**
     * Refresh token
     */
    AuthResponseDTO refreshToken(String refreshToken);

    /**
     * Verify token
     */
    boolean verifyToken(String token);
}
