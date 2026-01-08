package com.example.crudjob.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO dùng để tạo / cập nhật Permission
 */
@Data
public class PermissionRequestDTO {

    @NotBlank(message = "Permission name cannot be blank")
    private String name;

    @NotBlank(message = "API path cannot be blank")
    private String apiPath;

    @NotBlank(message = "HTTP method cannot be blank")
    private String method;
}
