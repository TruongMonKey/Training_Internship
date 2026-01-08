package com.example.crudjob.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.crudjob.dto.request.RoleRequestDTO;
import com.example.crudjob.dto.response.RoleResponseDTO;
import com.example.crudjob.entity.Permission;
import com.example.crudjob.entity.Role;
import com.example.crudjob.exception.ResourceNotFoundException;
import com.example.crudjob.repository.PermissionRepository;
import com.example.crudjob.repository.RoleRepository;
import com.example.crudjob.service.IRoleService;
import com.example.crudjob.utils.RoleMapper;

import lombok.RequiredArgsConstructor;

/**
 * Triển khai nghiệp vụ Role
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    private static final String ROLE_NOT_FOUND = "Role not found";

    /**
     * Tạo mới Role
     */
    @Override
    public RoleResponseDTO create(RoleRequestDTO dto) {

        if (roleRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Role already exists");
        }

        Role role = new Role();
        role.setName(dto.getName());

        if (dto.getPermissionIds() != null) {
            Set<Permission> permissions = permissionRepository.findAllById(dto.getPermissionIds())
                    .stream()
                    .collect(Collectors.toSet());

            role.setPermissions(permissions);
        }

        return RoleMapper.toResponse(roleRepository.save(role));
    }

    /**
     * Cập nhật Role
     */
    @Override
    public RoleResponseDTO update(Long id, RoleRequestDTO dto) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND));

        role.setName(dto.getName());

        if (dto.getPermissionIds() != null) {
            Set<Permission> permissions = permissionRepository.findAllById(dto.getPermissionIds())
                    .stream()
                    .collect(Collectors.toSet());

            role.setPermissions(permissions);
        }

        return RoleMapper.toResponse(roleRepository.save(role));
    }

    /**
     * Lấy Role theo ID
     */
    @Override
    public RoleResponseDTO getById(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND));

        return RoleMapper.toResponse(role);
    }

    /**
     * Lấy danh sách Role
     */
    @Override
    public List<RoleResponseDTO> getAll() {

        return roleRepository.findAll()
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    /**
     * Xóa Role
     */
    @Override
    public void delete(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND));

        roleRepository.delete(role);
    }
}
