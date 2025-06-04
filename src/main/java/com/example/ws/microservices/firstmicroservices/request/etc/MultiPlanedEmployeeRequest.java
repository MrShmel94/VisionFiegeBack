package com.example.ws.microservices.firstmicroservices.request.etc;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record MultiPlanedEmployeeRequest(
        @NotNull @Size(min = 1) List<Integer> employeeIds,
        @NotNull int planingId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @NotNull LocalDate dateTraining
        ) {
}
