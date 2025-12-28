package com.example.crudjob.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crudjob.dto.request.JobRequestDTO;
import com.example.crudjob.dto.response.ApiResponse;
import com.example.crudjob.dto.response.JobResponseDTO;
import com.example.crudjob.service.JobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job API", description = "CRUD Job Management")
public class JobController {

    private final JobService jobService;

    /* ================= CREATE ================= */
    @Operation(summary = "Create new job")
    @PostMapping
    /**
     * API tạo mới Job
     * @param dto Thông tin job cần tạo
     * @return ResponseEntity chứa ApiResponse với dữ liệu job vừa tạo
     */
    public ResponseEntity<ApiResponse<JobResponseDTO>> create(
            @Valid @RequestBody JobRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        jobService.create(dto),
                        "Job created successfully",
                        HttpStatus.CREATED.value()));
    }

    /* ================= GET ALL ================= */
    @Operation(summary = "Get all jobs with pagination")
    @GetMapping
    /**
     * API lấy danh sách job có hỗ trợ phân trang
     * @param page Trang bắt đầu (mặc định 0)
     * @param size Số lượng phần tử trên 1 trang (mặc định 10)
     * @return ResponseEntity chứa ApiResponse với thông tin phân trang và dữ liệu job
     */
    public ResponseEntity<ApiResponse<PageResponseDTO<JobResponseDTO>>> getAllWithPaging(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var pageData = jobService.getAll(pageable);
        return ResponseEntity.ok(
                ApiResponse.success(
                        new com.example.crudjob.dto.response.PageResponseDTO<>(pageData),
                        "Get job list successfully",
                        HttpStatus.OK.value()));
    }

    /* ================= GET BY ID ================= */
    @Operation(summary = "Get job by ID")
    @GetMapping("/{id}")
    /**
     * API lấy chi tiết thông tin 1 job theo ID
     * @param id Id của job
     * @return ResponseEntity chứa ApiResponse với dữ liệu của job
     */
    public ResponseEntity<ApiResponse<JobResponseDTO>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        jobService.getById(id),
                        "Get job successfully",
                        HttpStatus.OK.value()));
    }

    /* ================= UPDATE ================= */
    @Operation(summary = "Update job")
    @PutMapping("/{id}")
    /**
     * API cập nhật thông tin job
     * @param id Id của job cần cập nhật
     * @param dto Thông tin job cập nhật
     * @return ResponseEntity chứa ApiResponse với dữ liệu job đã cập nhật
     */
    public ResponseEntity<ApiResponse<JobResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody JobRequestDTO dto) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        jobService.update(id, dto),
                        "Job updated successfully",
                        HttpStatus.OK.value()));
    }

    /* ================= DELETE ================= */
    @Operation(summary = "Delete job")
    @DeleteMapping("/{id}")
    /**
     * API xoá job theo id
     * @param id Id của job
     * @return ResponseEntity xác nhận xoá thành công
     */
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {
        jobService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Job deleted successfully",
                        HttpStatus.OK.value()));
    }
}
