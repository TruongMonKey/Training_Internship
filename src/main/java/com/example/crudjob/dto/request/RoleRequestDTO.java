package com.example.crudjob.dto.request;

import java.util.Set;

import com.example.crudjob.entity.enums.ERole;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO dùng khi tạo / cập nhật Role
 */
@Data
public class RoleRequestDTO {

    @NotNull(message = "Role name must not be null")
    private ERole name;

    /**
     * Danh sách permission ID gán cho role
     */
    private Set<Long> permissionIds;
}
