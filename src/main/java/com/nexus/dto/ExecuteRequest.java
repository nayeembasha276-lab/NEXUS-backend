package com.nexus.dto;

import com.nexus.enums.AlgorithmType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExecuteRequest {

    @NotEmpty(message = "Dataset cannot be empty")
    @Size(max = 100000, message = "Dataset cannot contain more than 100,000 elements")
    private List<@NotNull Integer> numbers;

    private AlgorithmType mode;
}