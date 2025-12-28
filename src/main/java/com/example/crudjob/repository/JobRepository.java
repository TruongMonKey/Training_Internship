package com.example.crudjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crudjob.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}
