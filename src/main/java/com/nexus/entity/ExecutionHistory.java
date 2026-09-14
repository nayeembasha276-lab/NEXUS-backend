package com.nexus.entity;

import com.nexus.enums.AlgorithmType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "execution_history")
public class ExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlgorithmType algorithm;

    @Column(nullable = false)
    private int arraySize;

    @Column(nullable = false)
    private double chaosScore;

    @Column(nullable = false)
    private double sortedness;

    @Column(nullable = false)
    private long comparisons;

    @Column(nullable = false)
    private long swaps;

    @Column(nullable = false)
    private long executionTime;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    void onCreate() {
        if (executedAt == null) executedAt = LocalDateTime.now();
    }


}
