package com.example.crudjob.dto.request;

import com.example.crudjob.entity.enums.EJobStatus;
import com.example.crudjob.entity.enums.EJobType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class JobRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Location is required")
    private String location;

    @Min(value = 0, message = "Salary must be >= 0")
    private Integer salary;

    @NotNull(message = "Job type is required")
    private EJobType type;

    @NotNull(message = "Job status is required")
    private EJobStatus status;

    @Size(max = 1000, message = "Description max 1000 characters")
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public EJobType getType() {
        return type;
    }

    public void setType(EJobType type) {
        this.type = type;
    }

    public EJobStatus getStatus() {
        return status;
    }

    public void setStatus(EJobStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
