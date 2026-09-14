package com.nexus.service;

import com.nexus.algorithm.*;
import com.nexus.dto.*;
import com.nexus.entity.ExecutionHistory;
import com.nexus.entity.User;
import com.nexus.enums.AlgorithmType;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.repository.ExecutionHistoryRepository;
import com.nexus.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NexusService {

    private static final int MAX_INSERTION_DATASET_SIZE = 10_000;
    private static final int MAX_QUICK_DATASET_SIZE = 20_000;

    private final Map<AlgorithmType, SortingAlgorithm> algorithms;
    private final NexusDecisionEngine decisionEngine;
    private final ExecutionHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public NexusService(
            List<SortingAlgorithm> algorithmList,
            NexusDecisionEngine decisionEngine,
            ExecutionHistoryRepository historyRepository,
            UserRepository userRepository
    ) {
        this.algorithms = algorithmList.stream()
                .collect(Collectors.toMap(
                        SortingAlgorithm::getType,
                        a -> a
                ));

        this.decisionEngine = decisionEngine;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ExecutionResult execute(ExecuteRequest request) {

        List<Integer> numbers = request.getNumbers();

        /*
         * Analyze the dataset only once.
         */
        NexusDecisionEngine.DatasetAnalysis analysis =
                decisionEngine.analyze(numbers);

        AlgorithmType selected;
        String reason;

        /*
         * AUTO mode
         */
        if (request.getMode() == null ||
                request.getMode() == AlgorithmType.AUTO) {

            NexusDecisionEngine.Decision decision =
                    decisionEngine.decide(numbers, analysis);

            selected = decision.algorithm();
            reason = decision.reason();

        } else {

            /*
             * Manual algorithm selection
             */
            selected = request.getMode();
            reason = "Algorithm manually selected by the user.";
        }

        /*
         * Safety limits for expensive algorithms.
         */
        if (selected == AlgorithmType.INSERTION &&
                numbers.size() > MAX_INSERTION_DATASET_SIZE) {

            throw new IllegalArgumentException(
                    "Insertion Sort cannot be manually selected for datasets larger than 10,000 elements."
            );
        }

        if (selected == AlgorithmType.QUICK &&
                numbers.size() > MAX_QUICK_DATASET_SIZE) {

            throw new IllegalArgumentException(
                    "Quick Sort cannot be manually selected for datasets larger than 20,000 elements."
            );
        }

        SortingAlgorithm algorithm = algorithms.get(selected);

        if (algorithm == null) {
            throw new IllegalArgumentException(
                    "Unsupported algorithm: " + selected
            );
        }

        /*
         * Execute selected sorting algorithm.
         */
        SortMetrics metrics = algorithm.sort(numbers);

        /*
         * Save execution history.
         */
        ExecutionHistory history = new ExecutionHistory();

        history.setUser(currentUser());
        history.setAlgorithm(selected);
        history.setArraySize(numbers.size());
        history.setChaosScore(analysis.chaosScore());
        history.setSortedness(analysis.sortedness());
        history.setComparisons(metrics.getComparisons());
        history.setSwaps(metrics.getSwaps());
        history.setExecutionTime(metrics.getExecutionTime());

        historyRepository.save(history);

        /*
         * Build response.
         */
        ExecutionResult result = new ExecutionResult();

        result.setSelectedAlgorithm(selected);
        result.setSelectionReason(reason);
        result.setChaosScore(analysis.chaosScore());
        result.setSortedness(analysis.sortedness());
        result.setComparisons(metrics.getComparisons());
        result.setSwaps(metrics.getSwaps());
        result.setExecutionTime(metrics.getExecutionTime());
        result.setSortedArray(metrics.getSortedArray());

        return result;
    }

    public List<BenchmarkResult> benchmark(
            BenchmarkRequest request
    ) {

        List<BenchmarkResult> results = new ArrayList<>();

        for (AlgorithmType type : List.of(
                AlgorithmType.INSERTION,
                AlgorithmType.QUICK,
                AlgorithmType.MERGE,
                AlgorithmType.HEAP
        )) {

            SortMetrics metrics =
                    algorithms.get(type)
                            .sort(request.getNumbers());

            results.add(
                    new BenchmarkResult(
                            type,
                            metrics.getExecutionTime(),
                            metrics.getComparisons(),
                            metrics.getSwaps()
                    )
            );
        }

        return results;
    }

    /*
     * Optimized dashboard.
     *
     * Does NOT load the complete execution history into memory.
     * Database performs COUNT and AVG operations.
     */
    public DashboardResponse getDashboard() {

        User user = currentUser();

        DashboardResponse response =
                new DashboardResponse();

        long totalExecutions =
                historyRepository.countByUser(user);

        response.setTotalExecutions(totalExecutions);

        /*
         * No execution history.
         */
        if (totalExecutions == 0) {

            response.setAverageChaosScore(0);
            response.setAverageExecutionTime(0);
            response.setMostUsedAlgorithm(null);
            response.setAlgorithmUsage(
                    new LinkedHashMap<>()
            );

            return response;
        }

        /*
         * Database aggregation.
         */
        double averageChaosScore =
                historyRepository.averageChaosScoreByUser(user);

        double averageExecutionTime =
                historyRepository.averageExecutionTimeByUser(user);

        response.setAverageChaosScore(
                round(averageChaosScore)
        );

        response.setAverageExecutionTime(
                round(averageExecutionTime)
        );

        /*
         * Algorithm usage statistics.
         */
        Map<String, Long> usage =
                new LinkedHashMap<>();

        AlgorithmType mostUsed = null;
        long max = -1;

        for (AlgorithmType type : List.of(
                AlgorithmType.INSERTION,
                AlgorithmType.QUICK,
                AlgorithmType.MERGE,
                AlgorithmType.HEAP
        )) {

            long count =
                    historyRepository.countByUserAndAlgorithm(
                            user,
                            type
                    );

            usage.put(type.name(), count);

            if (count > max) {
                max = count;
                mostUsed = type;
            }
        }

        response.setAlgorithmUsage(usage);
        response.setMostUsedAlgorithm(mostUsed);

        return response;
    }

    /*
     * Paginated execution history.
     */
    public PagedHistoryResponse getHistory(
            int page,
            int size
    ) {

        int safePage = Math.max(page, 0);

        /*
         * Maximum page size = 50.
         */
        int safeSize = Math.min(
                Math.max(size, 1),
                50
        );

        Pageable pageable =
                PageRequest.of(
                        safePage,
                        safeSize
                );

        Page<ExecutionHistory> historyPage =
                historyRepository
                        .findAllByUserOrderByExecutedAtDesc(
                                currentUser(),
                                pageable
                        );

        List<HistoryResponse> content =
                historyPage.getContent()
                        .stream()
                        .map(this::toHistoryResponse)
                        .toList();

        return new PagedHistoryResponse(
                content,
                historyPage.getNumber(),
                historyPage.getSize(),
                historyPage.getTotalElements(),
                historyPage.getTotalPages(),
                historyPage.isFirst(),
                historyPage.isLast()
        );
    }

    @Transactional
    public void deleteHistory(Long id) {

        ExecutionHistory history =
                historyRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "History record not found: " + id
                                )
                        );

        /*
         * User can delete only their own history.
         */
        if (!history.getUser()
                .getId()
                .equals(currentUser().getId())) {

            throw new ResourceNotFoundException(
                    "History record not found: " + id
            );
        }

        historyRepository.delete(history);
    }

    @Transactional
    public void clearHistory() {

        historyRepository.deleteByUser(
                currentUser()
        );
    }

    private User currentUser() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found"
                        )
                );
    }

    private HistoryResponse toHistoryResponse(
            ExecutionHistory entity
    ) {

        HistoryResponse dto =
                new HistoryResponse();

        dto.setId(entity.getId());
        dto.setAlgorithm(entity.getAlgorithm());
        dto.setArraySize(entity.getArraySize());
        dto.setChaosScore(entity.getChaosScore());
        dto.setExecutionTime(entity.getExecutionTime());
        dto.setExecutedAt(entity.getExecutedAt());

        return dto;
    }

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }
}