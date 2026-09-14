package com.nexus.algorithm;

import com.nexus.enums.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InsertionSort implements SortingAlgorithm {

    @Override
    public AlgorithmType getType() { return AlgorithmType.INSERTION; }

    @Override
    public SortMetrics sort(List<Integer> input) {
        List<Integer> a = new ArrayList<>(input);
        long comparisons = 0, swaps = 0;
        long start = System.nanoTime();

        for (int i = 1; i < a.size(); i++) {
            int key = a.get(i);
            int j = i - 1;

            while (j >= 0) {
                comparisons++;
                if (a.get(j) > key) {
                    a.set(j + 1, a.get(j));
                    swaps++;
                    j--;
                } else {
                    break;
                }
            }
            a.set(j + 1, key);
        }

        return new SortMetrics(a, comparisons, swaps, System.nanoTime() - start);
    }
}
