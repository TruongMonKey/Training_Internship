package com.example.crudjob.entity;

import com.example.crudjob.entity.enums.EJobStatus;
import com.example.crudjob.entity.enums.EJobType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "jobs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 255, message = "Job title must be between 3 and 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    // BỎ VALIDATION cho company vì sau khi encrypt sẽ dài hơn 255
    // Validation đã được thực hiện ở JobRequestDTO
    @Column(nullable = false, columnDefinition = "TEXT")
    private String company;

    // BỎ VALIDATION cho location vì sau khi encrypt sẽ dài hơn 255
    // Validation đã được thực hiện ở JobRequestDTO
    @Column(nullable = false, columnDefinition = "TEXT")
    private String location;

    @NotNull(message = "Salary is required")
    @Min(value = 0, message = "Salary must be greater than or equal to 0")
    @Column(nullable = false)
    private Integer salary;

    @NotNull(message = "Job type is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EJobType type;

    @NotNull(message = "Job status is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EJobStatus status;

    // Description đã là TEXT, không cần thay đổi
    @Column(columnDefinition = "TEXT")
    private String description;

    // ===== Getter / Setter =====

    public Long getId() {
        return id;
    }

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