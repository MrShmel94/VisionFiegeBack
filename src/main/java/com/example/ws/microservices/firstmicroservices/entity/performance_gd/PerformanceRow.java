package com.example.ws.microservices.firstmicroservices.entity.performance_gd;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

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

    private Instant startActivity;
    private Instant endActivity;
    private BigDecimal duration;

    private Short ql;
    private Short qlBox;
    private Short qlHanging;
    private Short qlShoes;
    private Short qlBoots;
    private Short qlOther;
    private Short stowClarifications;
    private Short pickNos1;
    private Short pickNos2;


}
