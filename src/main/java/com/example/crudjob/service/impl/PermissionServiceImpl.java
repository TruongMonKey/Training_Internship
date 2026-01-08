package com.example.crudjob.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.crudjob.dto.request.PermissionRequestDTO;
import com.example.crudjob.dto.response.PermissionResponseDTO;
import com.example.crudjob.entity.Permission;
import com.example.crudjob.exception.ResourceNotFoundException;
import com.example.crudjob.repository.PermissionRepository;
import com.example.crudjob.service.IPermissionService;
import com.example.crudjob.utils.PermissionMapper;

import lombok.RequiredArgsConstructor;

/**
 * Triển khai nghiệp vụ Permission
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {

    private final PermissionRepository permissionRepository;

    private static final String PERMISSION_NOT_FOUND = "Permission not found";

    /**
     * Tạo mới Permission
     */
    @Override
    public PermissionResponseDTO create(PermissionRequestDTO dto) {

        if (permissionRepository.existsByApiPathAndMethod(
                dto.getApiPath(),
                dto.getMethod())) {

            throw new IllegalArgumentException(
                    "Permission already exists for this API and method");
        }

        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setApiPath(dto.getApiPath());
        permission.setMethod(dto.getMethod());

        return PermissionMapper.toResponse(
                permissionRepository.save(permission));
    }

    /**
     * Cập nhật Permission
     */
    @Override
    public PermissionResponseDTO update(
            Long id,
            PermissionRequestDTO dto) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERMISSION_NOT_FOUND));

        permission.setName(dto.getName());
        permission.setApiPath(dto.getApiPath());
        permission.setMethod(dto.getMethod());

        return PermissionMapper.toResponse(
                permissionRepository.save(permission));
    }

    /**
     * Lấy Permission theo ID
     */
    @Override
    public PermissionResponseDTO getById(Long id) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERMISSION_NOT_FOUND));

        return PermissionMapper.toResponse(permission);
    }

    /**
     * Lấy danh sách Permission
     */
    @Override
    public List<PermissionResponseDTO> getAll() {

        return permissionRepository.findAll()
                .stream()
                .map(PermissionMapper::toResponse)
                .toList();
    }

    /**
     * Xóa Permission
     */
    @Override
    public void delete(Long id) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERMISSION_NOT_FOUND));

        permissionRepository.delete(permission);
    }
}
