package com.example.crudjob.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Permission name cannot be blank")
    private String name;

    @NotBlank(message = "apiPath cannot be blank")
    private String apiPath;

    @NotBlank(message = "Method cannot be blank")
    private String method;

    @NotBlank(message = "Module cannot be blank")
    private String module;
}
