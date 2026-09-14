package com.nexus.dto;

import com.nexus.enums.AlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter

public class DashboardResponse {
    private long totalExecutions;
    private double averageChaosScore;
    private double averageExecutionTime;
    private AlgorithmType mostUsedAlgorithm;
    private Map<String, Long> algorithmUsage;
}
