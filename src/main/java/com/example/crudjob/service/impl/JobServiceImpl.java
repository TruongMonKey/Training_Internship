package com.example.crudjob.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.crudjob.dto.request.JobRequestDTO;
import com.example.crudjob.dto.response.JobResponseDTO;
import com.example.crudjob.entity.Job;
import com.example.crudjob.exception.ResourceNotFoundException;
import com.example.crudjob.repository.JobRepository;
import com.example.crudjob.service.IJobService;
import com.example.crudjob.service.EncryptionService;
import com.example.crudjob.utils.Mapper;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý nghiệp vụ Job
 * - Encrypt khi ghi DB
 * - Decrypt khi đọc DB
 */
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements IJobService {

    private final JobRepository jobRepository;
    private final EncryptionService encryptionService;

    private static final String JOB_NOT_FOUND = "Job not found";

    /* ================= CREATE ================= */

    @Override
    public JobResponseDTO create(JobRequestDTO dto) {

        Job job = Mapper.toEntity(dto);

        // Encrypt sensitive fields
        job.setCompany(encryptionService.encrypt(job.getCompany()));
        job.setLocation(encryptionService.encrypt(job.getLocation()));
        job.setDescription(encryptionService.encrypt(job.getDescription()));

        Job savedJob = jobRepository.save(job);

        return Mapper.toResponse(decryptJob(savedJob));
    }

    /* ================= READ ================= */

    @Override
    public List<JobResponseDTO> getAll() {

        return jobRepository.findAll()
                .stream()
                .map(this::decryptJob)
                .map(Mapper::toResponse)
                .toList();
    }

    @Override
    public Page<JobResponseDTO> getAll(Pageable pageable) {

        return jobRepository.findAll(pageable)
                .map(this::decryptJob)
                .map(Mapper::toResponse);
    }

    @Override
    public JobResponseDTO getById(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(JOB_NOT_FOUND));

        return Mapper.toResponse(decryptJob(job));
    }

    /* ================= UPDATE ================= */

    @Override
    public JobResponseDTO update(Long id, JobRequestDTO dto) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(JOB_NOT_FOUND));

        // Fields NOT encrypted (search / index)
        job.setTitle(dto.getTitle());
        job.setSalary(dto.getSalary());
        job.setType(dto.getType());
        job.setStatus(dto.getStatus());

        // Encrypted fields
        job.setCompany(encryptionService.encrypt(dto.getCompany()));
        job.setLocation(encryptionService.encrypt(dto.getLocation()));
        job.setDescription(encryptionService.encrypt(dto.getDescription()));

        Job updatedJob = jobRepository.save(job);

        return Mapper.toResponse(decryptJob(updatedJob));
    }

    /* ================= DELETE ================= */

    @Override
    public void delete(Long id) {

        jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(JOB_NOT_FOUND));

        jobRepository.deleteById(id);
    }

    /* ================= SEARCH ================= */

    @Override
    public Page<JobResponseDTO> searchByTitle(String title, Pageable pageable) {

        return jobRepository
                .findByTitleContainingIgnoreCase(title, pageable)
                .map(this::decryptJob)
                .map(Mapper::toResponse);
    }

    @Override
    public Page<JobResponseDTO> searchByCompany(String company, Pageable pageable) {

        // Vì company field được encrypt trong DB, không thể search trực tiếp bằng SQL
        // Giải pháp: Decrypt tất cả records trong memory và filter
        // Lưu ý: Cách này có thể ảnh hưởng performance với dataset lớn

        String searchTerm = company.toLowerCase();

        // Lấy tất cả jobs từ DB
        List<Job> allJobs = jobRepository.findAll();

        // Decrypt và filter theo company
        List<JobResponseDTO> filteredJobs = allJobs.stream()
                .map(this::decryptJob) // Decrypt company, location, description
                .filter(job -> job.getCompany().toLowerCase().contains(searchTerm))
                .map(Mapper::toResponse)
                .toList();

        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredJobs.size());
        List<JobResponseDTO> pageContent = filteredJobs.subList(start, end);

        // Tạo Page object với pagination metadata
        return new PageImpl<>(
                pageContent,
                pageable,
                filteredJobs.size());
    }

    /* ================= PRIVATE ================= */

    /**
     * Decrypt các field nhạy cảm sau khi đọc từ DB
     */
    private Job decryptJob(Job job) {

        job.setCompany(encryptionService.decrypt(job.getCompany()));
        job.setLocation(encryptionService.decrypt(job.getLocation()));
        job.setDescription(encryptionService.decrypt(job.getDescription()));

        return job;
    }
}