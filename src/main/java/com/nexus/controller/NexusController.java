package com.nexus.controller;

import com.nexus.dto.*;
import com.nexus.service.NexusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NexusController {

    private final NexusService nexusService;

    public NexusController(NexusService nexusService) {
        this.nexusService = nexusService;
    }

    @PostMapping("/execute")
    public ResponseEntity<ExecutionResult> execute(
            @Valid @RequestBody ExecuteRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nexusService.execute(request));
    }

    @PostMapping("/benchmark")
    public ResponseEntity<List<BenchmarkResult>> benchmark(
            @Valid @RequestBody BenchmarkRequest request
    ) {
        return ResponseEntity.ok(nexusService.benchmark(request));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(nexusService.getDashboard());
    }

    @GetMapping("/history")
    public ResponseEntity<PagedHistoryResponse> history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                nexusService.getHistory(page, size)
        );
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Map<String, String>> deleteHistory(@PathVariable Long id) {
        nexusService.deleteHistory(id);
        return ResponseEntity.ok(Map.of("message", "History record deleted successfully"));
    }

    @DeleteMapping("/history")
    public ResponseEntity<Map<String, String>> clearHistory() {
        nexusService.clearHistory();
        return ResponseEntity.ok(Map.of("message", "All history cleared successfully"));
    }
}
