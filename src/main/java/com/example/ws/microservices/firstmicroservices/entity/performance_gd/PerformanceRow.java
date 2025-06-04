package com.example.ws.microservices.firstmicroservices.entity.performance_gd;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceRow {

    private LocalDate date;
    private String expertis;
    private ActivityName activityName;
    private FinalCluster finalCluster;
    private SpiCluster activityCluster;

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
