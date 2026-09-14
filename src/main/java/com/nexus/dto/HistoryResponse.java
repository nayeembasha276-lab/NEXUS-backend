package com.nexus.dto;

import com.nexus.enums.AlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class HistoryResponse {
    private Long id;
    private AlgorithmType algorithm;
    private int arraySize;
    private double chaosScore;
    private long executionTime;
    private LocalDateTime executedAt;

}
