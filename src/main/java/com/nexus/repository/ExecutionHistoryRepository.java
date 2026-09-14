package com.nexus.repository;

import com.nexus.entity.ExecutionHistory;
import com.nexus.entity.User;
import com.nexus.enums.AlgorithmType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExecutionHistoryRepository extends JpaRepository<ExecutionHistory, Long> {

    // Pagination
    Page<ExecutionHistory> findAllByUserOrderByExecutedAtDesc(User user, Pageable pageable);

    // Existing method - currently can remain
    List<ExecutionHistory> findAllByUserOrderByExecutedAtDesc(User user);

    // Dashboard statistics
    long countByUser(User user);

    long countByUserAndAlgorithm(User user,AlgorithmType algorithm);

    // Average Chaos Score
    @Query("""
            select coalesce(avg(h.chaosScore), 0)
            from ExecutionHistory h
            where h.user = :user
            """)
    double averageChaosScoreByUser(@Param("user") User user);

    // Average Execution Time
    @Query("""
            select coalesce(avg(h.executionTime), 0)
            from ExecutionHistory h
            where h.user = :user
            """)
    double averageExecutionTimeByUser(@Param("user") User user);

    // Delete all history for current user
    void deleteByUser(User user);
}