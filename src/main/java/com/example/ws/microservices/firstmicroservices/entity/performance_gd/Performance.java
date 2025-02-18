package com.example.ws.microservices.firstmicroservices.entity.performance_gd;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
//@Entity
//@Table(name = "performance", schema = "performance_dg")
//@IdClass(PerformanceId.class)
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_seq_gen")
    @SequenceGenerator(name = "performance_seq_gen", sequenceName = "performance_seq", allocationSize = 1, schema = "performance_dg")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "expertis", nullable = false, length = 64)
    private String expertis;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_name_id", nullable = false)
    private ActivityName activityName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "final_cluster_id", nullable = false)
    private FinalCluster finalCluster;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_cluster_id", nullable = false)
    private SpiCluster activityCluster;

    @Column(name = "start_activity", nullable = false)
    private Instant startActivity;

    @Column(name = "end_activity", nullable = false)
    private Instant endActivity;

    @Column(name = "duration", nullable = false, precision = 10, scale = 3)
    private BigDecimal duration;

    @ColumnDefault("0")
    @Column(name = "ql")
    private Short ql;

    @ColumnDefault("0")
    @Column(name = "ql_box")
    private Short qlBox;

    @ColumnDefault("0")
    @Column(name = "ql_hanging")
    private Short qlHanging;

    @ColumnDefault("0")
    @Column(name = "ql_shoes")
    private Short qlShoes;

    @ColumnDefault("0")
    @Column(name = "ql_boots")
    private Short qlBoots;

    @ColumnDefault("0")
    @Column(name = "ql_other")
    private Short qlOther;

    @ColumnDefault("0")
    @Column(name = "stow_clarifications")
    private Short stowClarifications;

    @ColumnDefault("0")
    @Column(name = "pick_nos1")
    private Short pickNos1;

    @ColumnDefault("0")
    @Column(name = "pick_nos2")
    private Short pickNos2;

}