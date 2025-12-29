package com.example.crudjob.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.crudjob.dto.request.JobRequestDTO;
import com.example.crudjob.dto.response.ApiRes;
import com.example.crudjob.dto.response.JobResponseDTO;
import com.example.crudjob.dto.response.PageResponseDTO;
import com.example.crudjob.entity.constant.AppConstants;
import com.example.crudjob.service.JobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

/**
 * REST Controller quản lý các API liên quan đến công việc (Job)
 * 
 * Cung cấp các endpoint CRUD (Create, Read, Update, Delete) và tìm kiếm công
 * việc
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job API", description = "CRUD Job Management")
public class JobController {

        private final JobService jobService;

        /* ================= CREATE ================= */

        /**
         * Tạo mới một công việc
         * 
         * @param dto Dữ liệu công việc cần tạo
         * @return ResponseEntity chứa ApiResponse với thông tin công việc vừa tạo
         */
        @Operation(summary = "Create new job")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Job created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping
        public ResponseEntity<ApiRes<JobResponseDTO>> create(
                        @Valid @RequestBody JobRequestDTO dto) {
                JobResponseDTO createdJob = jobService.create(dto);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiRes.success(
                                                createdJob,
                                                AppConstants.JOB_CREATED_SUCCESS,
                                                HttpStatus.CREATED.value()));
        }

        /* ================= GET ALL ================= */

        /**
         * Lấy danh sách tất cả công việc có hỗ trợ phân trang
         * 
         * @param page Số trang (mặc định 0)
         * @param size Số lượng phần tử trên một trang (mặc định 10)
         * @return ResponseEntity chứa ApiResponse với danh sách công việc đã phân trang
         */
        @Operation(summary = "Get all jobs with pagination")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping
        public ResponseEntity<ApiRes<PageResponseDTO<JobResponseDTO>>> getAllWithPaging(
                        @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_STR) int page,
                        @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE_STR) int size) {
                var pageable = PageRequest.of(page, size);
                var pageData = jobService.getAll(pageable);
                PageResponseDTO<JobResponseDTO> response = new PageResponseDTO<>(pageData);
                return ResponseEntity.ok(
                                ApiRes.success(
                                                response,
                                                AppConstants.JOB_LIST_SUCCESS,
                                                HttpStatus.OK.value()));
        }

        /* ================= GET BY ID ================= */

        /**
         * Lấy chi tiết một công việc theo ID
         * 
         * @param id ID của công việc cần lấy
         * @return ResponseEntity chứa ApiResponse với thông tin chi tiết công việc
         */
        @Operation(summary = "Get job by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Job retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Job not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ApiRes<JobResponseDTO>> getById(
                        @PathVariable Long id) {
                JobResponseDTO job = jobService.getById(id);
                return ResponseEntity.ok(
                                ApiRes.success(
                                                job,
                                                AppConstants.JOB_GET_SUCCESS,
                                                HttpStatus.OK.value()));
        }

        /* ================= UPDATE ================= */

        /**
         * Cập nhật thông tin một công việc
         * 
         * @param id  ID của công việc cần cập nhật
         * @param dto Dữ liệu cập nhật của công việc
         * @return ResponseEntity chứa ApiResponse với thông tin công việc đã cập nhật
         */
        @Operation(summary = "Update job")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Job updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "404", description = "Job not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PutMapping("/{id}")
        public ResponseEntity<ApiRes<JobResponseDTO>> update(
                        @PathVariable Long id,
                        @Valid @RequestBody JobRequestDTO dto) {
                JobResponseDTO updatedJob = jobService.update(id, dto);
                return ResponseEntity.ok(
                                ApiRes.success(
                                                updatedJob,
                                                AppConstants.JOB_UPDATED_SUCCESS,
                                                HttpStatus.OK.value()));
        }

        /* ================= DELETE ================= */

        /**
         * Xoá một công việc theo ID
         * 
         * @param id ID của công việc cần xoá
         * @return ResponseEntity chứa ApiResponse xác nhận xoá thành công
         */
        @Operation(summary = "Delete job")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Job deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Job not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiRes<Void>> delete(
                        @PathVariable Long id) {
                jobService.delete(id);
                return ResponseEntity.ok(
                                ApiRes.success(
                                                null,
                                                AppConstants.JOB_DELETED_SUCCESS,
                                                HttpStatus.OK.value()));
        }

        /* ================= SEARCH ================= */

        /**
         * Tìm kiếm công việc theo tiêu đề
         * 
         * Tìm kiếm không phân biệt chữ hoa/thường, hỗ trợ tìm kiếm LIKE (chứa chuỗi)
         * 
         * @param title Tiêu đề công việc cần tìm kiếm
         * @param page  Số trang (mặc định 0)
         * @param size  Số lượng phần tử trên một trang (mặc định 10)
         * @return ResponseEntity chứa ApiResponse với danh sách công việc tìm được
         */
        @Operation(summary = "Search jobs by title")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Jobs found successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/search/title")
        public ResponseEntity<ApiRes<PageResponseDTO<JobResponseDTO>>> searchByTitle(
                        @Parameter(description = "Job title to search") @RequestParam String title,
                        @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_STR) int page,
                        @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE_STR) int size) {
                var pageable = PageRequest.of(page, size);
                var pageData = jobService.searchByTitle(title, pageable);
                PageResponseDTO<JobResponseDTO> response = new PageResponseDTO<>(pageData);
                return ResponseEntity.ok(
                                ApiRes.success(
                                                response,
                                                AppConstants.SEARCH_BY_TITLE_SUCCESS,
                                                HttpStatus.OK.value()));
        }

        /**
         * Tìm kiếm công việc theo tên công ty
         * 
         * Tìm kiếm không phân biệt chữ hoa/thường, hỗ trợ tìm kiếm LIKE (chứa chuỗi)
         * 
         * @param company Tên công ty cần tìm kiếm
         * @param page    Số trang (mặc định 0)
         * @param size    Số lượng phần tử trên một trang (mặc định 10)
         * @return ResponseEntity chứa ApiResponse với danh sách công việc tìm được
         */
        @Operation(summary = "Search jobs by company")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Jobs found successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/search/company")
        public ResponseEntity<ApiRes<PageResponseDTO<JobResponseDTO>>> searchByCompany(
                        @Parameter(description = "Company name to search") @RequestParam String company,
                        @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_STR) int page,
                        @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE_STR) int size) {
                var pageable = PageRequest.of(page, size);
                var pageData = jobService.searchByCompany(company, pageable);
                PageResponseDTO<JobResponseDTO> response = new PageResponseDTO<>(pageData);
                return ResponseEntity.ok(
                                ApiRes.success(
                                                response,
                                                AppConstants.SEARCH_BY_COMPANY_SUCCESS,
                                                HttpStatus.OK.value()));
        }
}
