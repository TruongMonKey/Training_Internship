package com.example.crudjob.config;

import com.example.crudjob.entity.User;
import com.example.crudjob.entity.enums.ERole;
import com.example.crudjob.repository.UserRepository;
import com.example.crudjob.service.JwtService;
import com.example.crudjob.service.RolePermissionResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter
 *
 * Intercepts every request and validates JWT token from Authorization header
 * Lấy roles từ database dựa trên userId trong token, sau đó resolve permissions
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RolePermissionResolver rolePermissionResolver;
    private final UserRepository userRepository; // THÊM UserRepository

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Lấy JWT token từ header
            String jwt = extractTokenFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtService.validateToken(jwt)) {
                // Parse token và lấy claims
                Claims claims = jwtService.parseToken(jwt);
                String username = claims.getSubject();
                Long userId = claims.get("userId", Long.class);

                if (userId == null) {
                    log.warn("Token does not contain userId");
                    filterChain.doFilter(request, response);
                    return;
                }

                // Lấy User từ database để lấy roles
                User user = userRepository.findById(userId)
                        .orElse(null);

                if (user == null) {
                    log.warn("User not found with userId: {}", userId);
                    filterChain.doFilter(request, response);
                    return;
                }

                // Lấy roles từ User entity
                List<String> roles = user.getRoles().stream()
                        .map(role -> role.getName().getValue())
                        .collect(Collectors.toList());

                // Resolve permissions từ roles (tự động)
                Collection<GrantedAuthority> authorities = rolePermissionResolver.resolveAuthorities(roles);

                // Tạo authentication token
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities);

                // Set authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT Token validated for user: {} (ID: {}) with {} roles and {} authorities", 
                        username, userId, roles.size(), authorities.size());
            }
        } catch (Exception e) {
            log.error("Could not validate JWT token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token từ Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}