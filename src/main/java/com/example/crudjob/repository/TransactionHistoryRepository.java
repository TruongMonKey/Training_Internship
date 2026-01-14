package com.example.crudjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crudjob.entity.TransactionHistory;

@Repository
public interface TransactionHistoryRepository
        extends JpaRepository<TransactionHistory, Long> {
}
