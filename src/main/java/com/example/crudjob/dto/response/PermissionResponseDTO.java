package com.example.crudjob.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO trả về thông tin Permission
 */
@Data
@AllArgsConstructor
public class PermissionResponseDTO {

    private Long id;
    private String name;
    private String apiPath;
    private String method;
}
