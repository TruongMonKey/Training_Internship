package com.example.crudjob.dto.response;

import com.example.crudjob.entity.enums.JobStatus;
import com.example.crudjob.entity.enums.JobType;

import lombok.Builder;

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

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public Integer getSalary() {
        return salary;
    }

    public JobType getType() {
        return type;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

}
