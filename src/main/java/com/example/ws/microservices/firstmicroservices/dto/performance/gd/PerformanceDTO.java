package com.example.ws.microservices.firstmicroservices.dto.performance.gd;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PerformanceDTO {

    private LocalDate date;
    private String expertis;
    private String activityName;
    private String spiCluster;
    private String finalCluster;
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
