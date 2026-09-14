package com.nexus.dto;

import com.nexus.enums.AlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class BenchmarkResult {
    private AlgorithmType algorithm;
    private long executionTime;
    private long comparisons;
    private long swaps;


}
