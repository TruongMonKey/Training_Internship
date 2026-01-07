package com.example.crudjob.config;

import com.example.crudjob.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Permission Interceptor
 *
 * Kiểm tra quyền truy cập endpoint dựa trên request path + HTTP method
 * So sánh với permissions của user lấy từ database
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionRepository permissionRepository;

    /**
     * Pre-handle: Kiểm tra permission trước khi request được xử lý
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String requestPath = request.getRequestURI();
        String httpMethod = request.getMethod();

        log.debug("Checking permission for: {} {}", httpMethod, requestPath);

        // Bỏ qua public endpoints
        if (isPublicEndpoint(requestPath)) {
            log.debug("Public endpoint - skipping permission check");
            return true;
        }

        // Get authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra user có authenticated không
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Request to {} {} without authentication", httpMethod, requestPath);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
            return false;
        }

        String username = authentication.getName();
        log.debug("Checking permission for user: {}", username);

        // Lấy authorities từ authentication
        // Authorities sẽ bao gồm cả ROLE và PERMISSION được resolve bởi
        // RolePermissionResolver
        var authorities = authentication.getAuthorities();

        // Kiểm tra xem user có permission match với request path + method không
        @SuppressWarnings("unchecked")
        boolean hasPermission = checkPermission(requestPath, httpMethod,
                (java.util.Collection<org.springframework.security.core.GrantedAuthority>) authorities);

        if (!hasPermission) {
            log.warn("User {} does not have permission for {} {}", username, httpMethod, requestPath);
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Access denied - insufficient permissions");
            return false;
        }

        log.info("Permission check PASSED for user: {} on {} {}", username, httpMethod, requestPath);
        return true;
    }

    /**
     * Send error response in JSON format
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        String jsonResponse = String.format("{\"success\":false,\"status\":%d,\"message\":\"%s\"}", status, message);
        response.getWriter().write(jsonResponse);
    }

    /**
     * Kiểm tra user có permission match với request path + method
     * 
     * Cách hoạt động:
     * 1. Lấy tất cả permissions từ database
     * 2. So sánh request path + method với permission.apiPath + permission.method
     * 3. Nếu match và user có authority với permission name → OK
     */
    private boolean checkPermission(String requestPath, String httpMethod,
            java.util.Collection<org.springframework.security.core.GrantedAuthority> authorities) {

        // Lấy tất cả permissions từ database
        List<com.example.crudjob.entity.Permission> allPermissions = permissionRepository.findAll();

        // Tìm permission match với request path + method
        for (com.example.crudjob.entity.Permission permission : allPermissions) {
            // Kiểm tra path + method match
            if (pathMatches(requestPath, permission.getApiPath()) &&
                    httpMethod.equalsIgnoreCase(permission.getMethod())) {

                // Kiểm tra user có authority của permission này không
                String permissionName = permission.getName();
                boolean userHasPermission = authorities.stream()
                        .anyMatch(auth -> auth.getAuthority().equals(permissionName));

                if (userHasPermission) {
                    log.debug("Permission matched: {} - {} {}", permissionName, httpMethod, requestPath);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Path matching logic
     * Support:
     * - Exact match: /api/jobs
     * - Wildcard: /api/jobs/** hoặc /api/{id}
     */
    private boolean pathMatches(String requestPath, String patternPath) {
        // Exact match
        if (requestPath.equals(patternPath)) {
            return true;
        }

        // Wildcard match - /api/jobs/**
        if (patternPath.endsWith("/**")) {
            String basePath = patternPath.substring(0, patternPath.length() - 3);
            return requestPath.startsWith(basePath);
        }

        // Path variable match - /api/jobs/{id} → /api/jobs/1
        String pattern = patternPath.replaceAll("\\{[^}]+\\}", "[^/]+");
        return requestPath.matches(pattern);
    }

    /**
     * Xác định endpoint nào là public (không cần check permission)
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/actuator") ||
                path.equals("/");
    }
}
