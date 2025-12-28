package com.example.crudjob.utils;

import com.example.crudjob.dto.request.JobRequestDTO;
import com.example.crudjob.dto.response.JobResponseDTO;
import com.example.crudjob.entity.Job;

public class JobMapper {

    public static Job toEntity(JobRequestDTO dto) {
        return Job.builder()
                .title(dto.getTitle())
                .company(dto.getCompany())
                .location(dto.getLocation())
                .salary(dto.getSalary())
                .type(dto.getType())
                .status(dto.getStatus())
                .description(dto.getDescription())
                .build();
    }

    public static JobResponseDTO toResponse(Job job) {
        return JobResponseDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .salary(job.getSalary())
                .type(job.getType())
                .status(job.getStatus())
                .description(job.getDescription())
                .build();
    }
}
