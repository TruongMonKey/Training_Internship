package com.example.crudjob.service;

import com.example.crudjob.entity.enums.ERole;
import com.example.crudjob.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * RolePermissionResolver
 *
 * Maps roles to permissions dynamically
 * Không cần lưu permissions trong token - lấy từ database dựa vào role
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionResolver {

    private final RoleRepository roleRepository;

    /**
     * Resolve permissions từ roles
     * - Input: ["ROLE_USER", "ROLE_ADMIN"]
     * - Output: [GrantedAuthority(ROLE_USER), GrantedAuthority(ROLE_ADMIN),
     * GrantedAuthority(VIEW_JOBS), ...]
     */
    public Collection<GrantedAuthority> resolveAuthorities(Collection<String> roleNames) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (roleNames == null || roleNames.isEmpty()) {
            log.warn("No roles found");
            return authorities;
        }

        // Thêm roles làm authorities
        roleNames.forEach(roleName -> {
            authorities.add(new SimpleGrantedAuthority(roleName));
        });

        // Lấy permissions từ role entity và thêm vào authorities
        roleNames.forEach(roleName -> {
            try {
                ERole eRole = ERole.fromValue(roleName);
                roleRepository.findByName(eRole).ifPresent(role -> {
                    role.getPermissions()
                            .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));
                    log.debug("Resolved {} permissions for role: {}", role.getPermissions().size(), roleName);
                });
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role name: {}", roleName);
            }
        });

        return authorities;
    }

    /**
     * Get permissions cho một role cụ thể
     */
    public Set<String> getPermissionsForRole(String roleName) {
        Set<String> permissions = new HashSet<>();

        try {
            ERole eRole = ERole.fromValue(roleName);
            roleRepository.findByName(eRole).ifPresent(
                    role -> role.getPermissions().forEach(permission -> permissions.add(permission.getName())));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid role name: {}", roleName);
        }

        return permissions;
    }
}
