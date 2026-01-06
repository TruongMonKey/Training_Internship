package com.example.crudjob.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.crudjob.dto.request.JobRequestDTO;
import com.example.crudjob.dto.response.JobResponseDTO;
import com.example.crudjob.entity.Job;
import com.example.crudjob.exception.ResourceNotFoundException;
import com.example.crudjob.repository.JobRepository;
import com.example.crudjob.service.IJobService;
import com.example.crudjob.utils.JobMapper;

import lombok.RequiredArgsConstructor;

/**
 * Lớp Service triển khai các logic nghiệp vụ liên quan đến công việc (Job)
 */
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements IJobService {

    private final JobRepository jobRepository;

    private static final String JOB_NOT_FOUND = "Job not found";

    /**
     * {@inheritDoc}
     */
    @Override
    public JobResponseDTO create(JobRequestDTO dto) {
        Job job = JobMapper.toEntity(dto);
        Job savedJob = jobRepository.save(job);
        return JobMapper.toResponse(savedJob);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JobResponseDTO> getAll() {
        return jobRepository.findAll()
                .stream()
                .map(JobMapper::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<JobResponseDTO> getAll(Pageable pageable) {
        return jobRepository.findAll(pageable).map(JobMapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobResponseDTO getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(JOB_NOT_FOUND));
        return JobMapper.toResponse(job);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobResponseDTO update(Long id, JobRequestDTO dto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(JOB_NOT_FOUND));

        job.setTitle(dto.getTitle());
        job.setCompany(dto.getCompany());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());
        job.setType(dto.getType());
        job.setStatus(dto.getStatus());
        job.setDescription(dto.getDescription());

        Job updatedJob = jobRepository.save(job);
        return JobMapper.toResponse(updatedJob);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(JOB_NOT_FOUND));
        jobRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<JobResponseDTO> searchByTitle(String title, Pageable pageable) {
        return jobRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(JobMapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<JobResponseDTO> searchByCompany(String company, Pageable pageable) {
        return jobRepository.findByCompanyContainingIgnoreCase(company, pageable)
                .map(JobMapper::toResponse);
    }
}
