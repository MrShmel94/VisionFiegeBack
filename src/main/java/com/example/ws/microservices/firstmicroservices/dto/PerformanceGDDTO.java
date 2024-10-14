package com.example.ws.microservices.firstmicroservices.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class PerformanceGDDTO {
    private Long performanceId;
    private String expertis;
    private Long activityNameId;
    private Long finalClusterId;
    private Long spiClusterId;
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
