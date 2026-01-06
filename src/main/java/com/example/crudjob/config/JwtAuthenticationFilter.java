package com.example.crudjob.config;

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

/**
 * JWT Authentication Filter
 *
 * Intercepts every request and validates JWT token from Authorization header
 * Resolves permissions dynamically from roles using RolePermissionResolver
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RolePermissionResolver rolePermissionResolver;

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

                // Lấy roles từ claims
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);

                // Resolve permissions từ roles (tự động)
                Collection<GrantedAuthority> authorities = rolePermissionResolver.resolveAuthorities(roles);

                // Tạo authentication token
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
                        null, authorities);

                // Set authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT Token validated for user: {} with {} authorities", username, authorities.size());
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
