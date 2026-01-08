package com.example.crudjob.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.crudjob.dto.request.RoleRequestDTO;
import com.example.crudjob.dto.response.RoleResponseDTO;
import com.example.crudjob.service.IRoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST API quản lý Role
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Role API", description = "CRUD API for Role management")
public class RoleController {

    private final IRoleService roleService;

    @Operation(summary = "Create new role")
    @PostMapping
    public ResponseEntity<RoleResponseDTO> create(
            @Valid @RequestBody RoleRequestDTO dto) {

        return new ResponseEntity<>(
                roleService.create(dto),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update role by ID")
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDTO dto) {

        return ResponseEntity.ok(roleService.update(id, dto));
    }

    @Operation(summary = "Get role by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(roleService.getById(id));
    }

    @Operation(summary = "Get all roles")
    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> getAll() {

        return ResponseEntity.ok(roleService.getAll());
    }

    @Operation(summary = "Delete role by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
