package com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceRowDTO {
    private LocalDate date;
    private String expertis;
    private String activityName;
    private String finalCluster;
    private String activityCluster;

    private LocalDateTime startActivity;
    private LocalDateTime endActivity;
    private double duration;
    private double durationIdle;
    private int idleCount;

    private int ql;
    private int qlBox;
    private int qlHanging;
    private int qlShoes;
    private int qlBoots;
    private int qlOther;
    private int stowClarifications;
    private int pickNos1;
    private int pickNos2;
}