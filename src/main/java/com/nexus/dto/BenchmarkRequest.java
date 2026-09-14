package com.nexus.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BenchmarkRequest {

    @NotEmpty(message = "Dataset cannot be empty")
    @Size(max = 5000, message = "Benchmark dataset cannot contain more than 5,000 elements")
    private List<@NotNull Integer> numbers;
}