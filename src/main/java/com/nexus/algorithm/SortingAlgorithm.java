package com.nexus.algorithm;

import com.nexus.enums.AlgorithmType;
import java.util.List;

public interface SortingAlgorithm {
    AlgorithmType getType();
    SortMetrics sort(List<Integer> input);
}
