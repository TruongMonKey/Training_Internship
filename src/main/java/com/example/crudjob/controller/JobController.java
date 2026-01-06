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
import com.example.crudjob.service.IJobService;
import com.example.crudjob.utils.AppConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

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
         * @param page Page number (default is 0)
         * @param size Number of items per page (default is 10)
         * @return ResponseEntity containing paginated job list
         */
        @Operation(summary = "Get all jobs with pagination", description = "Retrieve all jobs with pagination support using page number and page size")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully with pagination metadata"),
                        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters. Page or size may be negative or exceed limits"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
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
         * Retrieve job details by ID
         *
         * @param id ID of the job to retrieve
         * @return ResponseEntity containing job details
         */
        @Operation(summary = "Get job by ID", description = "Retrieve full details of a specific job using its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Job retrieved successfully"),
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

        /* ================= SEARCH ================= */

        /**
         * Search jobs by title
         *
         * Case-insensitive search with LIKE (contains) support
         *
         * @param title Job title to search
         * @param page  Page number (default is 0)
         * @param size  Page size (default is 10)
         * @return ResponseEntity containing paginated search results
         */
        @Operation(summary = "Search jobs by title", description = "Search jobs by title using case-insensitive and partial match (LIKE) with pagination")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
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
         * Search jobs by company name
         *
         * Case-insensitive search with LIKE (contains) support
         *
         * @param company Company name to search
         * @param page    Page number (default is 0)
         * @param size    Page size (default is 10)
         * @return ResponseEntity containing paginated search results
         */
        @Operation(summary = "Search jobs by company", description = "Search jobs by company name using case-insensitive and partial match (LIKE) with pagination")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
                        @ApiResponse(responseCode = "500", description = "Internal server error. Please try again later")
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
