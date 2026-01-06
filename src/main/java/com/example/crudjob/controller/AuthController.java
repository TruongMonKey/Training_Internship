package com.example.crudjob.controller;

import com.example.crudjob.dto.request.LoginRequestDTO;
import com.example.crudjob.dto.request.RegisterRequestDTO;
import com.example.crudjob.dto.response.ApiRes;
import com.example.crudjob.dto.response.AuthResponseDTO;
import com.example.crudjob.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication APIs
 *
 * Provides endpoints for user login, registration, token refresh and
 * verification
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication API", description = "User authentication and authorization")
public class AuthController {

    private final IAuthService authService;

    /**
     * User login endpoint
     *
     * @param request Login credentials (username and password)
     * @return AuthResponseDTO with access and refresh tokens
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username and password")
    public ResponseEntity<ApiRes<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login request for username: {}", request.getUsername());

        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(
                ApiRes.success(response, "Login successful", HttpStatus.OK.value()));
    }

    /**
     * User registration endpoint
     *
     * @param request Registration data (username, password, confirm password)
     * @return AuthResponseDTO with access and refresh tokens
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<ApiRes<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Register request for username: {}", request.getUsername());

        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiRes.success(response, "Registration successful", HttpStatus.CREATED.value()));
    }

    /**
     * Refresh access token endpoint
     *
     * @param refreshToken Refresh token from login/register response
     * @return AuthResponseDTO with new access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiRes<AuthResponseDTO>> refreshToken(
            @RequestParam String refreshToken) {
        log.info("Refresh token request");

        AuthResponseDTO response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(
                ApiRes.success(response, "Token refreshed successfully", HttpStatus.OK.value()));
    }

    /**
     * Token verification endpoint
     *
     * @param token JWT token to verify
     * @return true if token is valid, false otherwise
     */
    @GetMapping("/verify")
    @Operation(summary = "Verify token", description = "Check if JWT token is valid")
    public ResponseEntity<ApiRes<Boolean>> verifyToken(@RequestParam String token) {
        log.info("Token verification request");

        boolean isValid = authService.verifyToken(token);
        String message = isValid ? "Token is valid" : "Token is invalid";
        return ResponseEntity.ok(
                ApiRes.success(isValid, message, HttpStatus.OK.value()));
    }
}
