package com.example.crudjob.dto.response;

import java.util.Set;

import com.example.crudjob.entity.enums.ERole;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO trả về thông tin Role
 */
@Data
@AllArgsConstructor
public class RoleResponseDTO {

    private Long id;
    private ERole name;
    private Set<String> permissions;
}
