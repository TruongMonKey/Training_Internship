package com.example.crudjob.dto.request;

import com.example.crudjob.entity.enums.EJobStatus;
import com.example.crudjob.entity.enums.EJobType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequestDTO {

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 255, message = "Company must be between 2 and 255 characters")
    private String company;

    @NotBlank(message = "Job location is required")
    @Size(min = 2, max = 255, message = "Location must be between 2 and 255 characters")
    private String location;

    @NotNull(message = "Salary is required")
    @Min(value = 0, message = "Salary must be greater than or equal to 0")
    private Integer salary;

    @NotNull(message = "Job type is required")
    private EJobType type;

    @NotNull(message = "Job status is required")
    private EJobStatus status;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
}
