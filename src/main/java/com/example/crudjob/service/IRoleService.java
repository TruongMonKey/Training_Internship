package com.example.crudjob.service;

import java.util.List;

import com.example.crudjob.dto.request.RoleRequestDTO;
import com.example.crudjob.dto.response.RoleResponseDTO;

/**
 * Interface định nghĩa nghiệp vụ Role
 */
public interface IRoleService {

    RoleResponseDTO create(RoleRequestDTO dto);

    RoleResponseDTO update(Long id, RoleRequestDTO dto);

    RoleResponseDTO getById(Long id);

    List<RoleResponseDTO> getAll();

    void delete(Long id);
}
