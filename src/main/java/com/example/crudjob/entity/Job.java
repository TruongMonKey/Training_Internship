package com.example.crudjob.entity;

import com.example.crudjob.entity.enums.EJobStatus;
import com.example.crudjob.entity.enums.EJobType;
import jakarta.persistence.*;
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

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String company;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false)
    private Integer salary;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EJobType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EJobStatus status;

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
