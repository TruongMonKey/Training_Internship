package com.example.crudjob.service;

import java.util.List;

import com.example.crudjob.dto.request.PermissionRequestDTO;
import com.example.crudjob.dto.response.PermissionResponseDTO;

/**
 * Interface định nghĩa nghiệp vụ Permission
 */
public interface IPermissionService {

    PermissionResponseDTO create(PermissionRequestDTO dto);

    PermissionResponseDTO update(Long id, PermissionRequestDTO dto);

    PermissionResponseDTO getById(Long id);

    List<PermissionResponseDTO> getAll();

    void delete(Long id);

}
