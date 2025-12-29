package com.example.crudjob.dto.response;

import com.example.crudjob.entity.enums.JobStatus;
import com.example.crudjob.entity.enums.JobType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobResponseDTO {

    private Long id;
    private String title;
    private String company;
    private String location;
    private Integer salary;
    private JobType type;
    private JobStatus status;
    private String description;

}
