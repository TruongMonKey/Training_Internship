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

import com.example.crudjob.constant.AppConstants;
import com.example.crudjob.dto.request.JobRequestDTO;
import com.example.crudjob.dto.response.ApiRes;
import com.example.crudjob.dto.response.JobResponseDTO;
import com.example.crudjob.dto.response.PageResponseDTO;
import com.example.crudjob.service.IJobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated; // THÊM IMPORT NÀY

/**
 * REST Controller for managing Job-related APIs
 *
 * Provides CRUD (Create, Read, Update, Delete) endpoints
 * and job search functionalities.
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job API", description = "CRUD Job Management")
@Validated // THÊM ANNOTATION NÀY để validation request parameters hoạt động
public class JobController {

        private final IJobService jobService;

        /* ================= CREATE ================= */

        /**
         * Create a new job
         *
         * @param dto Job data to be created
         * @return ResponseEntity containing ApiResponse with created job information
         */
        @Operation(summary = "Create new job", description = "Create a new job using the data provided in the request body")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Job created successfully. Returns the created job with an auto-generated ID"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data. Required fields may be missing or improperly formatted"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User lacks required permissions to create jobs"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
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
         * Retrieve all jobs with pagination support
         *
         * @param page Page number (default is 0, must be >= 0)
         * @param size Number of items per page (default is 10, must be 1-100)
         * @return ResponseEntity containing paginated job list
         */
        @Operation(summary = "Get all jobs with pagination", description = "Retrieve all jobs with pagination support using page number and page size")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully with pagination metadata"),
                        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters. Page must be >= 0, size must be 1-100"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User lacks required permissions"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
        })
        @GetMapping
        public ResponseEntity<ApiRes<PageResponseDTO<JobResponseDTO>>> getAllWithPaging(
                        @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_STR) @Min(value = 0, message = "Page must be >= 0") int page,
                        @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE_STR) @Min(value = 1, message = "Size must be >= 1") @Max(value = 100, message = "Size cannot exceed 100 records per page") int size) {

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
         * Retrieve job details by ID
         *
         * @param id ID of the job to retrieve
         * @return ResponseEntity containing job details
         */
        @Operation(summary = "Get job by ID", description = "Retrieve full details of a specific job using its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Job retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User lacks required permissions"),
                        @ApiResponse(responseCode = "404", description = "Job not found with the provided ID"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
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
         * Update an existing job
         *
         * @param id  ID of the job to update
         * @param dto Updated job data
         * @return ResponseEntity containing updated job information
         */
        @Operation(summary = "Update job", description = "Update an existing job by providing its ID and new data")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Job updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User lacks required permissions to update jobs"),
                        @ApiResponse(responseCode = "404", description = "Job not found with the provided ID"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
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
         * Delete a job by ID
         *
         * @param id ID of the job to delete
         * @return ResponseEntity confirming successful deletion
         */
        @Operation(summary = "Delete job", description = "Delete a job from the system. This action is irreversible")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Job deleted successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User lacks required permissions to delete jobs"),
                        @ApiResponse(responseCode = "404", description = "Job not found with the provided ID"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
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

        /* ================= SEARCH (MUST BE BEFORE GET BY ID) ================= */

        /**
         * Search jobs by title
         *
         * Case-insensitive search with LIKE (contains) support.
         * NOTE: This endpoint must be defined BEFORE @GetMapping("/{id}")
         * to prevent Spring from treating "search" as an ID.
         *
         * @param title Job title to search (required, 1-255 characters)
         * @param page  Page number (default is 0, must be >= 0)
         * @param size  Page size (default is 10, must be 1-100)
         * @return ResponseEntity containing paginated search results
         */
        @Operation(summary = "Search jobs by title", description = "Search jobs by title using case-insensitive and partial match (LIKE) with pagination")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid search parameters. Title cannot be blank, page >= 0, size 1-100"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User lacks required permissions"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
        })
        @GetMapping("/search/title")
        public ResponseEntity<ApiRes<PageResponseDTO<JobResponseDTO>>> searchByTitle(
                        @Parameter(description = "Job title to search (required, 1-255 characters)") @RequestParam @NotBlank(message = "Title cannot be blank") @Size(min = 1, max = 255, message = "Title must be 1-255 characters") String title,
                        @Parameter(description = "Page number (default 0, must be >= 0)") @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_STR) @Min(value = 0, message = "Page must be >= 0") int page,
                        @Parameter(description = "Page size (default 10, must be 1-100)") @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE_STR) @Min(value = 1, message = "Size must be >= 1") @Max(value = 100, message = "Size cannot exceed 100 records per page") int size) {

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
         * Search jobs by company name
         *
         * Case-insensitive search with LIKE (contains) support.
         * NOTE: Since company field is encrypted in database, this search
         * decrypts all records in memory and filters them. This approach
         * works but may have performance implications for large datasets.
         * 
         * NOTE: This endpoint must be defined BEFORE @GetMapping("/{id}")
         * to prevent Spring from treating "search" as an ID.
         *
         * @param company Company name to search (required, 1-255 characters)
         * @param page    Page number (default is 0, must be >= 0)
         * @param size    Page size (default is 10, must be 1-100)
         * @return ResponseEntity containing paginated search results
         */
        @Operation(summary = "Search jobs by company", description = "Search jobs by company name using case-insensitive and partial match (LIKE) with pagination. Note: This searches encrypted data by decrypting in memory, which may impact performance for large datasets.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid search parameters. Company cannot be blank, page >= 0, size 1-100"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User lacks required permissions"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
        })
        @GetMapping("/search/company")
        public ResponseEntity<ApiRes<PageResponseDTO<JobResponseDTO>>> searchByCompany(
                        @Parameter(description = "Company name to search (required, 1-255 characters)") @RequestParam @NotBlank(message = "Company cannot be blank") @Size(min = 1, max = 255, message = "Company must be 1-255 characters") String company,
                        @Parameter(description = "Page number (default 0, must be >= 0)") @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_STR) @Min(value = 0, message = "Page must be >= 0") int page,
                        @Parameter(description = "Page size (default 10, must be 1-100)") @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE_STR) @Min(value = 1, message = "Size must be >= 1") @Max(value = 100, message = "Size cannot exceed 100 records per page") int size) {

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