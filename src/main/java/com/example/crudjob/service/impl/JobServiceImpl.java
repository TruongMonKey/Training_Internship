package com.example.crudjob.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.crudjob.dto.request.JobRequestDTO;
import com.example.crudjob.dto.response.JobResponseDTO;
import com.example.crudjob.entity.Job;
import com.example.crudjob.repository.JobRepository;
import com.example.crudjob.service.JobService;
import com.example.crudjob.utils.JobMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    /**
     * {@inheritDoc}
     */
    public JobResponseDTO create(JobRequestDTO dto) {
        Job job = JobMapper.toEntity(dto);
        return JobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<JobResponseDTO> getAll() {
        return jobRepository.findAll()
                .stream()
                .map(JobMapper::toResponse)
                .toList();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public org.springframework.data.domain.Page<JobResponseDTO> getAll(org.springframework.data.domain.Pageable pageable) {
        return jobRepository.findAll(pageable).map(com.example.crudjob.utils.JobMapper::toResponse);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public JobResponseDTO getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return JobMapper.toResponse(job);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public JobResponseDTO update(Long id, JobRequestDTO dto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setTitle(dto.getTitle());
        job.setCompany(dto.getCompany());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());
        job.setType(dto.getType());
        job.setStatus(dto.getStatus());
        job.setDescription(dto.getDescription());

        return JobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void delete(Long id) {
        jobRepository.deleteById(id);
    }
}
