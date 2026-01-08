package com.example.crudjob.repository;

import com.example.crudjob.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Tìm permission theo name
     */
    Optional<Permission> findByName(String name);

    /**
     * Tìm permission theo apiPath + method
     */
    Optional<Permission> findByApiPathAndMethod(
            String apiPath,
            String method);

    /**
     * Kiểm tra permission đã tồn tại hay chưa
     */
    boolean existsByApiPathAndMethod(
            String apiPath,
            String method);
}
