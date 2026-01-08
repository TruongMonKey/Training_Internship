package com.example.crudjob.service.impl;

import com.example.crudjob.dto.request.LoginRequestDTO;
import com.example.crudjob.dto.request.RegisterRequestDTO;
import com.example.crudjob.dto.response.AuthResponseDTO;
import com.example.crudjob.entity.Role;
import com.example.crudjob.entity.User;
import com.example.crudjob.entity.enums.ERole;
import com.example.crudjob.exception.BadRequestException;
import com.example.crudjob.exception.ResourceNotFoundException;
import com.example.crudjob.repository.RoleRepository;
import com.example.crudjob.repository.UserRepository;
import com.example.crudjob.service.IAuthService;
import com.example.crudjob.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Đăng nhập user
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // Tìm user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Kiểm tra password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", request.getUsername());
            throw new BadRequestException("Invalid username or password");
        }

        log.info("User {} logged in successfully", request.getUsername());

        // Tạo tokens
        return generateAuthResponse(user);
    }

    /**
     * Đăng ký user mới
     */
    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Registration attempt for username: {}", request.getUsername());

        // Kiểm tra user đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new BadRequestException("Username already exists");
        }

        // Kiểm tra password trùng khớp
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Gán default role (USER)
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        userRepository.save(user);
        log.info("User {} registered successfully", request.getUsername());

        return generateAuthResponse(user);
    }

    /**
     * Refresh token
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO refreshToken(String refreshToken) {
        log.info("Refreshing token");

        // Verify refresh token
        if (!jwtService.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        // Lấy userId từ token
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        String username = jwtService.getUsernameFromToken(refreshToken);

        if (userId == null || username == null) {
            throw new BadRequestException("Invalid refresh token claims");
        }

        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return generateAuthResponse(user);
    }

    /**
     * Verify token
     */
    @Override
    @Transactional(readOnly = true)
    public boolean verifyToken(String token) {
        return jwtService.validateToken(token);
    }

    /**
     * Helper method: Generate auth response với tokens
     * Token chỉ chứa userId và username, không chứa roles
     */
    private AuthResponseDTO generateAuthResponse(User user) {
        // Lấy roles (chỉ để return trong response, không lưu vào token)
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().getValue())
                .collect(Collectors.toSet());

        // Lấy permissions từ roles (chỉ để return trong response, không lưu vào token)
        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());

        // Generate tokens (CHỈ lưu userId và username, KHÔNG lưu roles)
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

        return AuthResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .roles(roles)
                .permissions(permissions)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000) // Convert to seconds
                .build();
    }
}