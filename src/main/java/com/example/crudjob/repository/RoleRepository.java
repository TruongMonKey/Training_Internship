package com.example.crudjob.repository;

import com.example.crudjob.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Tìm role theo name
     */
    Optional<Role> findByName(String name);
}
