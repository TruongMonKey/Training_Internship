package com.example.crudjob.service;

import java.util.List;

import com.example.crudjob.dto.request.JobRequestDTO;
import com.example.crudjob.dto.response.JobResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobService {

    /**
     * Tạo mới Job
     */
    /**
     * Tạo mới một job từ thông tin đầu vào
     * 
     * @param dto thông tin job cần tạo
     * @return JobResponseDTO dữ liệu job vừa tạo
     */
    JobResponseDTO create(JobRequestDTO dto);

    /**
     * Lấy danh sách tất cả Job
     */
    /**
     * Lấy toàn bộ danh sách job (không phân trang)
     * 
     * @return Danh sách JobResponseDTO
     */
    List<JobResponseDTO> getAll();

    /**
     * Lấy danh sách Job có phân trang
     */
    /**
     * Lấy danh sách job có phân trang
     * 
     * @param pageable thông tin phân trang (page/size,...)
     * @return Đối tượng Page<JobResponseDTO> chứa dữ liệu theo trang
     */
    Page<JobResponseDTO> getAll(Pageable pageable);

    /**
     * Lấy Job theo ID
     */
    /**
     * Lấy chi tiết job theo id
     * 
     * @param id id của job
     * @return JobResponseDTO thông tin job
     */
    JobResponseDTO getById(Long id);

    /**
     * Cập nhật Job
     */
    /**
     * Cập nhật job theo id với thông tin mới
     * 
     * @param id  id của job sẽ cập nhật
     * @param dto thông tin mới của job
     * @return JobResponseDTO thông tin job đã cập nhật
     */
    JobResponseDTO update(Long id, JobRequestDTO dto);

    /**
     * Xoá Job
     */
    /**
     * Xoá job theo id
     * 
     * @param id id của job
     */
    void delete(Long id);

    /**
     * Tìm kiếm job theo tiêu đề
     */
    Page<JobResponseDTO> searchByTitle(String title, Pageable pageable);

    /**
     * Tìm kiếm job theo công ty
     */
    Page<JobResponseDTO> searchByCompany(String company, Pageable pageable);
}
