package com.nexus.algorithm;

import java.util.List;

public class SortMetrics {
    private final List<Integer> sortedArray;
    private final long comparisons;
    private final long swaps;
    private final long executionTime;

    public SortMetrics(List<Integer> sortedArray, long comparisons, long swaps, long executionTime) {
        this.sortedArray = sortedArray;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.executionTime = executionTime;
    }

    public List<Integer> getSortedArray() { return sortedArray; }
    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public long getExecutionTime() { return executionTime; }
}
