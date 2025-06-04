package com.example.ws.microservices.firstmicroservices.request.etc;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Getter
@Setter
public class PlaningTrainingRequest {
    @NotNull private Long documentId;
    @NotNull private String documentName;
    @NotNull @Min(1) private Integer maxSize;
    @NotNull private LocalDate date;
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
    @NotNull private List<String> positions;
    @NotNull private List<String> trainers;
    @NotNull private String description;
    @NotNull private Boolean isAutoTraining;
    @NotNull private String place;
}
