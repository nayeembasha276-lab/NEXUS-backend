package com.nexus.algorithm;

import com.nexus.enums.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuickSort implements SortingAlgorithm {

    @Override
    public AlgorithmType getType() {
        return AlgorithmType.QUICK;
    }

    @Override
    public SortMetrics sort(List<Integer> input) {

        List<Integer> a = new ArrayList<>(input);
        Metrics metrics = new Metrics();

        long start = System.nanoTime();

        quickSort(a, 0, a.size() - 1, metrics);

        long executionTime = System.nanoTime() - start;

        return new SortMetrics(
                a,
                metrics.comparisons,
                metrics.swaps,
                executionTime
        );
    }

    private void quickSort(
            List<Integer> a,
            int low,
            int high,
            Metrics metrics
    ) {

        if (low >= high) {
            return;
        }

        int p = partition(a, low, high, metrics);

        quickSort(a, low, p - 1, metrics);
        quickSort(a, p + 1, high, metrics);
    }

    private int partition(
            List<Integer> a,
            int low,
            int high,
            Metrics metrics
    ) {

        int pivot = a.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {

            metrics.comparisons++;

            if (a.get(j) <= pivot) {
                i++;
                swap(a, i, j, metrics);
            }
        }

        swap(a, i + 1, high, metrics);

        return i + 1;
    }

    private void swap(
            List<Integer> a,
            int i,
            int j,
            Metrics metrics
    ) {

        if (i != j) {

            int temp = a.get(i);

            a.set(i, a.get(j));
            a.set(j, temp);

            metrics.swaps++;
        }
    }

    private static class Metrics {

        private long comparisons;
        private long swaps;
    }
}