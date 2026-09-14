package com.nexus.algorithm;

import com.nexus.enums.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MergeSort implements SortingAlgorithm {

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.MERGE;
    }

    @Override
    public SortMetrics sort(List<Integer> input) {

        List<Integer> a = new ArrayList<>(input);

        // Execution-local metrics.
        // Safe for concurrent requests.
        Metrics metrics = new Metrics();

        long start = System.nanoTime();

        mergeSort(a, 0, a.size() - 1, metrics);

        long executionTime = System.nanoTime() - start;

        return new SortMetrics(
                a,
                metrics.comparisons,
                metrics.swaps,
                executionTime
        );
    }

    private void mergeSort(
            List<Integer> a,
            int left,
            int right,
            Metrics metrics
    ) {

        if (left >= right) {
            return;
        }

        int mid = left + (right - left) / 2;

        mergeSort(a, left, mid, metrics);
        mergeSort(a, mid + 1, right, metrics);

        merge(a, left, mid, right, metrics);
    }

    private void merge(
            List<Integer> a,
            int left,
            int mid,
            int right,
            Metrics metrics
    ) {

        List<Integer> temp =
                new ArrayList<>(right - left + 1);

        int i = left;
        int j = mid + 1;

        while (i <= mid && j <= right) {

            metrics.comparisons++;

            if (a.get(i) <= a.get(j)) {
                temp.add(a.get(i++));
            } else {
                temp.add(a.get(j++));
            }
        }

        while (i <= mid) {
            temp.add(a.get(i++));
        }

        while (j <= right) {
            temp.add(a.get(j++));
        }

        for (int k = 0; k < temp.size(); k++) {

            a.set(left + k, temp.get(k));

            metrics.swaps++;
        }
    }

    /**
     * Metrics belong to one execution only.
     */
    private static class Metrics {

        private long comparisons;
        private long swaps;
    }
}