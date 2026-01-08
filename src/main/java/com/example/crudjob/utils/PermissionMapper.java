package com.example.crudjob.utils;

import com.example.crudjob.dto.response.PermissionResponseDTO;
import com.example.crudjob.entity.Permission;

/**
 * Mapper chuyển Permission Entity → Response DTO
 */
public class PermissionMapper {

    public static PermissionResponseDTO toResponse(Permission permission) {

        return new PermissionResponseDTO(
                permission.getId(),
                permission.getName(),
                permission.getApiPath(),
                permission.getMethod());
    }
}
