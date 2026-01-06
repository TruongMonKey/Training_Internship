package com.example.crudjob.repository;

import com.example.crudjob.entity.Role;
import com.example.crudjob.entity.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Tìm role theo enum
     */
    Optional<Role> findByName(ERole name);
}
