package com.example.crudjob.config;

import com.example.crudjob.entity.Permission;
import com.example.crudjob.entity.Role;
import com.example.crudjob.entity.enums.ERole;
import com.example.crudjob.repository.PermissionRepository;
import com.example.crudjob.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Initialization Configuration
 *
 * Initializes default roles and permissions in database on application startup
 * Pattern: Master-Detail (Permissions created first, then assigned to Roles)
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataInitConfig {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * Initialize default roles and permissions
     */
    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info(">>> START INITIALIZING DATABASE");

            long countPermissions = permissionRepository.count();
            long countRoles = roleRepository.count();

            // Step 1: Initialize permissions if not exist
            if (countPermissions == 0) {
                log.info("Initializing permissions...");
                initializePermissions();
                log.info("Permissions initialized successfully");
            } else {
                log.info("Permissions already exist - skipping initialization");
            }

            // Step 2: Initialize roles if not exist
            if (countRoles == 0) {
                log.info("Initializing roles...");
                initializeRoles();
                log.info("Roles initialized successfully");
            } else {
                log.info("Roles already exist - skipping initialization");
            }

            log.info(">>> END INITIALIZING DATABASE");
        };
    }

    /**
     * Create all permissions for all modules
     */
    private void initializePermissions() {
        List<Permission> permissions = new ArrayList<>();

        // ===== JOB PERMISSIONS =====
        permissions.add(createPermission("Create a job", "/api/jobs", "POST", "JOBS"));
        permissions.add(createPermission("Update a job", "/api/jobs/{id}", "PUT", "JOBS"));
        permissions.add(createPermission("Delete a job", "/api/jobs/{id}", "DELETE", "JOBS"));
        permissions.add(createPermission("Get a job by id", "/api/jobs/{id}", "GET", "JOBS"));
        permissions.add(createPermission("Get all jobs", "/api/jobs", "GET", "JOBS"));

        // ===== USER PERMISSIONS =====
        permissions.add(createPermission("Create a user", "/api/users", "POST", "USERS"));
        permissions.add(createPermission("Update a user", "/api/users/{id}", "PUT", "USERS"));
        permissions.add(createPermission("Delete a user", "/api/users/{id}", "DELETE", "USERS"));
        permissions.add(createPermission("Get a user by id", "/api/users/{id}", "GET", "USERS"));
        permissions.add(createPermission("Get all users", "/api/users", "GET", "USERS"));

        // ===== ROLE PERMISSIONS =====
        permissions.add(createPermission("Create a role", "/api/roles", "POST", "ROLES"));
        permissions.add(createPermission("Update a role", "/api/roles/{id}", "PUT", "ROLES"));
        permissions.add(createPermission("Delete a role", "/api/roles/{id}", "DELETE", "ROLES"));
        permissions.add(createPermission("Get a role by id", "/api/roles/{id}", "GET", "ROLES"));
        permissions.add(createPermission("Get all roles", "/api/roles", "GET", "ROLES"));

        // ===== PERMISSION PERMISSIONS =====
        permissions.add(createPermission("Create a permission", "/api/permissions", "POST", "PERMISSIONS"));
        permissions.add(createPermission("Update a permission", "/api/permissions/{id}", "PUT", "PERMISSIONS"));
        permissions.add(createPermission("Delete a permission", "/api/permissions/{id}", "DELETE", "PERMISSIONS"));
        permissions.add(createPermission("Get a permission by id", "/api/permissions/{id}", "GET", "PERMISSIONS"));
        permissions.add(createPermission("Get all permissions", "/api/permissions", "GET", "PERMISSIONS"));

        permissionRepository.saveAll(permissions);
    }

    /**
     * Create all roles with their assigned permissions
     */
    private void initializeRoles() {
        List<Permission> allPermissions = permissionRepository.findAll();

        // Create ADMIN role with all permissions
        if (!roleRepository.findByName(ERole.ROLE_ADMIN).isPresent()) {
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            adminRole.setPermissions(new HashSet<>(allPermissions));
            roleRepository.save(adminRole);
            log.info("Created ROLE_ADMIN with {} permissions", allPermissions.size());
        }

        // Create USER role with limited permissions (GET only)
        if (!roleRepository.findByName(ERole.ROLE_USER).isPresent()) {
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER);

            Set<Permission> userPermissions = new HashSet<>();
            // Add only GET permissions for USER role
            allPermissions.stream()
                    .filter(p -> "GET".equals(p.getMethod()))
                    .forEach(userPermissions::add);

            userRole.setPermissions(userPermissions);
            roleRepository.save(userRole);
            log.info("Created ROLE_USER with {} GET permissions", userPermissions.size());
        }

        // Create MANAGER role with moderate permissions (GET, POST, PUT)
        if (!roleRepository.findByName(ERole.ROLE_MANAGER).isPresent()) {
            Role managerRole = new Role();
            managerRole.setName(ERole.ROLE_MANAGER);

            Set<Permission> managerPermissions = new HashSet<>();
            // Add GET, POST, PUT permissions for MANAGER role
            allPermissions.stream()
                    .filter(p -> "GET".equals(p.getMethod()) ||
                            "POST".equals(p.getMethod()) ||
                            "PUT".equals(p.getMethod()))
                    .forEach(managerPermissions::add);

            managerRole.setPermissions(managerPermissions);
            roleRepository.save(managerRole);
            log.info("Created ROLE_MANAGER with {} permissions", managerPermissions.size());
        }
    }

    /**
     * Helper method to create a permission object
     */
    private Permission createPermission(String name, String apiPath, String method, String module) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setApiPath(apiPath);
        permission.setMethod(method);
        permission.setModule(module);
        return permission;
    }
}
