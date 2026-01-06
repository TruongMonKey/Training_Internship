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

import java.util.HashSet;
import java.util.Set;

/**
 * Data Initialization Configuration
 *
 * Initializes default roles and permissions in database on application startup
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
            log.info("Initializing default roles and permissions");

            // Create permissions with API path and method
            Permission viewJobs = createPermissionIfNotExists("VIEW_JOBS", "GET", "/api/jobs/**", "Job");
            Permission createJobs = createPermissionIfNotExists("CREATE_JOBS", "POST", "/api/jobs", "Job");
            Permission editJobs = createPermissionIfNotExists("EDIT_JOBS", "PUT", "/api/jobs/**", "Job");
            Permission deleteJobs = createPermissionIfNotExists("DELETE_JOBS", "DELETE", "/api/jobs/**", "Job");
            Permission manageUsers = createPermissionIfNotExists("MANAGE_USERS", "GET", "/api/admin/users/**", "User");
            Permission manageRoles = createPermissionIfNotExists("MANAGE_ROLES", "POST", "/api/admin/roles", "Role");

            // Create USER role
            if (!roleRepository.findByName(ERole.ROLE_USER).isPresent()) {
                Role userRole = new Role();
                userRole.setName(ERole.ROLE_USER);
                Set<Permission> userPermissions = new HashSet<>();
                userPermissions.add(viewJobs);
                userRole.setPermissions(userPermissions);
                roleRepository.save(userRole);
                log.info("Created ROLE_USER with permissions");
            }

            // Create ADMIN role
            if (!roleRepository.findByName(ERole.ROLE_ADMIN).isPresent()) {
                Role adminRole = new Role();
                adminRole.setName(ERole.ROLE_ADMIN);
                Set<Permission> adminPermissions = new HashSet<>();
                adminPermissions.add(viewJobs);
                adminPermissions.add(createJobs);
                adminPermissions.add(editJobs);
                adminPermissions.add(deleteJobs);
                adminPermissions.add(manageUsers);
                adminPermissions.add(manageRoles);
                adminRole.setPermissions(adminPermissions);
                roleRepository.save(adminRole);
                log.info("Created ROLE_ADMIN with all permissions");
            }

            log.info("Data initialization completed");
        };
    }

    /**
     * Helper method to create permission if not exists
     * with API path and HTTP method
     */
    private Permission createPermissionIfNotExists(String permissionName, String httpMethod,
            String apiPath, String module) {
        return permissionRepository.findByName(permissionName)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(permissionName);
                    permission.setMethod(httpMethod);
                    permission.setApiPath(apiPath);
                    permission.setModule(module);
                    return permissionRepository.save(permission);
                });
    }
}
