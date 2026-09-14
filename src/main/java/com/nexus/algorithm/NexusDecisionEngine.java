package com.nexus.algorithm;

import com.nexus.enums.AlgorithmType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NexusDecisionEngine {

    private static final int MAX_DATASET_SIZE = 100_000;

    public DatasetAnalysis analyze(List<Integer> numbers) {

        validateDataset(numbers);

        int n = numbers.size();

        if (n <= 1) {
            return new DatasetAnalysis(0, 100);
        }

        /*
         * O(n log n) inversion counting
         * instead of the previous O(n²) nested loops.
         */
        int[] array = numbers.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        int[] temp = new int[n];

        long inversions = countInversions(array, temp, 0, n - 1);

        long maxPairs = (long) n * (n - 1) / 2;

        double chaos = maxPairs == 0
                ? 0
                : (inversions * 100.0) / maxPairs;

        double sortedness = 100.0 - chaos;

        return new DatasetAnalysis(
                round(chaos),
                round(sortedness)
        );
    }

    public Decision decide(List<Integer> numbers) {

        DatasetAnalysis analysis = analyze(numbers);

        return decide(numbers, analysis);
    }

    /*
     * Uses already calculated analysis.
     * This avoids analyzing the same dataset twice.
     */
    public Decision decide(
            List<Integer> numbers,
            DatasetAnalysis analysis
    ) {

        int n = numbers.size();

        /*
         * Large dataset check comes BEFORE nearly-sorted check.
         *
         * This prevents a huge dataset from selecting
         * Insertion Sort just because it happens to be sorted.
         */
        if (n >= 5000) {
            return new Decision(
                    AlgorithmType.HEAP,
                    "Large dataset detected. Heap Sort provides predictable O(n log n) worst-case performance."
            );
        }

        if (n <= 32 || analysis.sortedness >= 85) {
            return new Decision(
                    AlgorithmType.INSERTION,
                    "Small or nearly sorted dataset detected. Insertion Sort is efficient for this input."
            );
        }

        if (analysis.chaosScore >= 70) {
            return new Decision(
                    AlgorithmType.QUICK,
                    "High disorder detected. Quick Sort is selected for strong average-case performance."
            );
        }

        return new Decision(
                AlgorithmType.MERGE,
                "Moderate disorder detected. Merge Sort provides stable O(n log n) performance."
        );
    }

    /*
     * Merge Sort based inversion counting.
     *
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    private long countInversions(
            int[] array,
            int[] temp,
            int left,
            int right
    ) {

        if (left >= right) {
            return 0;
        }

        int mid = left + (right - left) / 2;

        long inversions = 0;

        inversions += countInversions(
                array,
                temp,
                left,
                mid
        );

        inversions += countInversions(
                array,
                temp,
                mid + 1,
                right
        );

        inversions += mergeAndCount(
                array,
                temp,
                left,
                mid,
                right
        );

        return inversions;
    }

    private long mergeAndCount(
            int[] array,
            int[] temp,
            int left,
            int mid,
            int right
    ) {

        int i = left;
        int j = mid + 1;
        int k = left;

        long inversions = 0;

        while (i <= mid && j <= right) {

            if (array[i] <= array[j]) {
                temp[k++] = array[i++];
            } else {

                temp[k++] = array[j++];

                /*
                 * Every remaining element in the left half
                 * forms an inversion with array[j].
                 */
                inversions += (mid - i + 1);
            }
        }

        while (i <= mid) {
            temp[k++] = array[i++];
        }

        while (j <= right) {
            temp[k++] = array[j++];
        }

        for (int index = left; index <= right; index++) {
            array[index] = temp[index];
        }

        return inversions;
    }

    private void validateDataset(List<Integer> numbers) {

        if (numbers == null) {
            throw new IllegalArgumentException(
                    "Dataset cannot be null."
            );
        }

        if (numbers.size() > MAX_DATASET_SIZE) {
            throw new IllegalArgumentException(
                    "Dataset cannot contain more than "
                            + MAX_DATASET_SIZE
                            + " elements."
            );
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record DatasetAnalysis(
            double chaosScore,
            double sortedness
    ) {
    }

    public record Decision(
            AlgorithmType algorithm,
            String reason
    ) {
    }
}