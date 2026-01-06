package com.example.crudjob.config;

import com.example.crudjob.entity.Permission;
import com.example.crudjob.entity.Role;
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

            // Create permissions if not exist
            Permission viewJobs = createPermissionIfNotExists("VIEW_JOBS");
            Permission createJobs = createPermissionIfNotExists("CREATE_JOBS");
            Permission editJobs = createPermissionIfNotExists("EDIT_JOBS");
            Permission deleteJobs = createPermissionIfNotExists("DELETE_JOBS");
            Permission manageUsers = createPermissionIfNotExists("MANAGE_USERS");
            Permission manageRoles = createPermissionIfNotExists("MANAGE_ROLES");

            // Create USER role
            if (!roleRepository.findByName("ROLE_USER").isPresent()) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                Set<Permission> userPermissions = new HashSet<>();
                userPermissions.add(viewJobs);
                userRole.setPermissions(userPermissions);
                roleRepository.save(userRole);
                log.info("Created ROLE_USER");
            }

            // Create ADMIN role
            if (!roleRepository.findByName("ROLE_ADMIN").isPresent()) {
                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                Set<Permission> adminPermissions = new HashSet<>();
                adminPermissions.add(viewJobs);
                adminPermissions.add(createJobs);
                adminPermissions.add(editJobs);
                adminPermissions.add(deleteJobs);
                adminPermissions.add(manageUsers);
                adminPermissions.add(manageRoles);
                adminRole.setPermissions(adminPermissions);
                roleRepository.save(adminRole);
                log.info("Created ROLE_ADMIN");
            }

            log.info("Data initialization completed");
        };
    }

    /**
     * Helper method to create permission if not exists
     */
    private Permission createPermissionIfNotExists(String permissionName) {
        return permissionRepository.findByName(permissionName)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(permissionName);
                    return permissionRepository.save(permission);
                });
    }
}
