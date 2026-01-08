package com.example.crudjob.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.crudjob.dto.request.PermissionRequestDTO;
import com.example.crudjob.dto.response.PermissionResponseDTO;
import com.example.crudjob.service.IPermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST API quản lý Permission
 */
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Permission API", description = "CRUD API for Permission management")
public class PermissionController {

    private final IPermissionService permissionService;

    @Operation(summary = "Create new permission")
    @PostMapping
    public ResponseEntity<PermissionResponseDTO> create(
            @Valid @RequestBody PermissionRequestDTO dto) {
        return new ResponseEntity<>(
                permissionService.create(dto),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Update permission by ID")
    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequestDTO dto) {
        return ResponseEntity.ok(
                permissionService.update(id, dto));
    }

    @Operation(summary = "Get permission by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                permissionService.getById(id));
    }

    @Operation(summary = "Get all permissions")
    @GetMapping
    public ResponseEntity<List<PermissionResponseDTO>> getAll() {
        return ResponseEntity.ok(
                permissionService.getAll());
    }

    @Operation(summary = "Delete permission by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
