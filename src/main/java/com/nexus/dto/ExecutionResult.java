package com.nexus.dto;

import com.nexus.enums.AlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResult {
    private AlgorithmType selectedAlgorithm;
    private String selectionReason;
    private double chaosScore;
    private double sortedness;
    private long comparisons;
    private long swaps;
    private long executionTime;
    private List<Integer> sortedArray;

}
