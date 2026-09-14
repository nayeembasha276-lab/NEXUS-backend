package com.nexus.algorithm;

import com.nexus.enums.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HeapSort implements SortingAlgorithm {

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.HEAP;
    }

    @Override
    public SortMetrics sort(List<Integer> input) {

        List<Integer> a = new ArrayList<>(input);

        // Execution-local metrics.
        // Each request gets its own counters.
        long comparisons = 0;
        long swaps = 0;

        long start = System.nanoTime();

        int n = a.size();

        for (int i = n / 2 - 1; i >= 0; i--) {
            long[] metrics = heapify(a, n, i, comparisons, swaps);
            comparisons = metrics[0];
            swaps = metrics[1];
        }

        for (int i = n - 1; i > 0; i--) {

            if (0 != i) {
                int temp = a.get(0);
                a.set(0, a.get(i));
                a.set(i, temp);
                swaps++;
            }

            long[] metrics = heapify(a, i, 0, comparisons, swaps);
            comparisons = metrics[0];
            swaps = metrics[1];
        }

        long executionTime = System.nanoTime() - start;

        return new SortMetrics(
                a,
                comparisons,
                swaps,
                executionTime
        );
    }

    private long[] heapify(
            List<Integer> a,
            int n,
            int i,
            long comparisons,
            long swaps
    ) {

        int largest = i;

        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n) {

            comparisons++;

            if (a.get(left) > a.get(largest)) {
                largest = left;
            }
        }

        if (right < n) {

            comparisons++;

            if (a.get(right) > a.get(largest)) {
                largest = right;
            }
        }

        if (largest != i) {

            int temp = a.get(i);
            a.set(i, a.get(largest));
            a.set(largest, temp);

            swaps++;

            return heapify(
                    a,
                    n,
                    largest,
                    comparisons,
                    swaps
            );
        }

        return new long[]{comparisons, swaps};
    }
}