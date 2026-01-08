package com.example.crudjob.utils;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.crudjob.dto.response.RoleResponseDTO;
import com.example.crudjob.entity.Role;

/**
 * Mapper chuyển Role Entity → Response DTO
 */
public class RoleMapper {

    public static RoleResponseDTO toResponse(Role role) {

        Set<String> permissions = role.getPermissions()
                .stream()
                .map(p -> p.getName())
                .collect(Collectors.toSet());

        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                permissions);
    }
}
