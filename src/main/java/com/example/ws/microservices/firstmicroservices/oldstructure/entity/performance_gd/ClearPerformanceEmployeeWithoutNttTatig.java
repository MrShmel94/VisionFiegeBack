package com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.attendance.ShiftTimeWork;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "clear_performance_employee_without_ntt_tatig", schema = "performance_gd")
public class ClearPerformanceEmployeeWithoutNttTatig implements ClearPerformanceData{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clear_perf_seq")
    @SequenceGenerator(name = "clear_perf_seq",
            sequenceName = "performance_gd.clear_performance_employee_without_ntt_id_seq",
            allocationSize = 200)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Size(max = 64)
    @NotNull
    @Column(name = "expertis", nullable = false, length = 64)
    private String expertis;

    @ColumnDefault("0")
    @Column(name = "ql_opt")
    private int qlOpt;

    @ColumnDefault("0")
    @Column(name = "time_opt")
    private double timeOpt;

    @ColumnDefault("0")
    @Column(name = "support_opt")
    private double supportOpt;

    @ColumnDefault("0")
    @Column(name = "ntt_opt")
    private double nttOpt;

    @ColumnDefault("0")
    @Column(name = "ql_multi")
    private int qlMulti;

    @ColumnDefault("0")
    @Column(name = "time_multi")
    private double timeMulti;

    @ColumnDefault("0")
    @Column(name = "ntt_multi")
    private double nttMulti;

    @ColumnDefault("0")
    @Column(name = "ql_single")
    private int qlSingle;

    @ColumnDefault("0")
    @Column(name = "time_single")
    private double timeSingle;

    @ColumnDefault("0")
    @Column(name = "ntt_single")
    private double nttSingle;

    @ColumnDefault("0")
    @Column(name = "support_pack")
    private double supportPack;

    @ColumnDefault("0")
    @Column(name = "ntt_pack")
    private double nttPack;

    @ColumnDefault("0")
    @Column(name = "ql_sort")
    private int qlSort;

    @ColumnDefault("0")
    @Column(name = "time_sort")
    private double timeSort;

    @ColumnDefault("0")
    @Column(name = "support_sort")
    private double supportSort;

    @ColumnDefault("0")
    @Column(name = "ntt_sort")
    private double nttSort;

    @ColumnDefault("0")
    @Column(name = "ql_wmo")
    private int qlWmo;

    @ColumnDefault("0")
    @Column(name = "time_wmo")
    private double timeWmo;

    @ColumnDefault("0")
    @Column(name = "support_wmo")
    private double supportWmo;

    @ColumnDefault("0")
    @Column(name = "ntt_wmo")
    private double nttWmo;

    @ColumnDefault("0")
    @Column(name = "ql_nco")
    private int qlNco;

    @ColumnDefault("0")
    @Column(name = "time_nco")
    private double timeNco;

    @ColumnDefault("0")
    @Column(name = "support_nco")
    private double supportNco;

    @ColumnDefault("0")
    @Column(name = "ntt_nco")
    private double nttNco;

    @ColumnDefault("0")
    @Column(name = "ql_stow")
    private int qlStow;

    @ColumnDefault("0")
    @Column(name = "time_stow")
    private double timeStow;

    @ColumnDefault("0")
    @Column(name = "support_stow")
    private double supportStow;

    @ColumnDefault("0")
    @Column(name = "ntt_stow")
    private double nttStow;

    @ColumnDefault("0")
    @Column(name = "ql_pick")
    private int qlPick;

    @ColumnDefault("0")
    @Column(name = "time_pick")
    private double timePick;

    @ColumnDefault("0")
    @Column(name = "support_pick")
    private double supportPick;

    @ColumnDefault("0")
    @Column(name = "ntt_pick")
    private double nttPick;

    @ColumnDefault("0")
    @Column(name = "support_shipping")
    private double supportShipping;

    @ColumnDefault("0")
    @Column(name = "ntt_shipping")
    private double nttShipping;

    @ColumnDefault("0")
    @Column(name = "ql_return")
    private int qlReturn;

    @ColumnDefault("0")
    @Column(name = "time_return")
    private double timeReturn;

    @ColumnDefault("0")
    @Column(name = "support_return")
    private double supportReturn;

    @ColumnDefault("0")
    @Column(name = "ntt_return")
    private double nttReturn;

    @ColumnDefault("0")
    @Column(name = "ql_fast")
    private int qlFast;

    @ColumnDefault("0")
    @Column(name = "time_fast")
    private double timeFast;

    @ColumnDefault("0")
    @Column(name = "ntt_fast")
    private double nttFast;

    @ColumnDefault("0")
    @Column(name = "ql_repack")
    private int qlRepack;

    @ColumnDefault("0")
    @Column(name = "time_repack")
    private double timeRepack;

    @ColumnDefault("0")
    @Column(name = "support_repack")
    private double supportRepack;

    @ColumnDefault("0")
    @Column(name = "ql_repl")
    private int qlRepl;

    @ColumnDefault("0")
    @Column(name = "time_repl")
    private double timeRepl;

    @ColumnDefault("0")
    @Column(name = "ql_core")
    private int qlCore;

    @ColumnDefault("0")
    @Column(name = "ql_core_boxed")
    private int qlCoreBoxed;

    @ColumnDefault("0")
    @Column(name = "ql_core_hanging")
    private int qlCoreHanging;

    @ColumnDefault("0")
    @Column(name = "ql_core_shoes")
    private int qlCoreShoes;

    @ColumnDefault("0")
    @Column(name = "ql_core_boots")
    private int qlCoreBoots;

    @ColumnDefault("0")
    @Column(name = "ql_core_other")
    private int qlCoreOther;

    @ColumnDefault("0")
    @Column(name = "time_core")
    private double timeCore;

    @ColumnDefault("0")
    @Column(name = "support_core")
    private double supportCore;

    @ColumnDefault("0")
    @Column(name = "ntt_core")
    private double nttCore;

    @ColumnDefault("0")
    @Column(name = "support_goods")
    private double supportGoods;

    @ColumnDefault("0")
    @Column(name = "ntt_goods")
    private double nttGoods;

    @ColumnDefault("0")
    @Column(name = "duration_idle")
    private double durationIdle;

    @ColumnDefault("0")
    @Column(name = "idle_count")
    private int idleCount;

    @ColumnDefault("0")
    @Column(name = "ql_relocation")
    private int qlRelocation;

    @ColumnDefault("0")
    @Column(name = "time_relocation")
    private double timeRelocation;

    @ColumnDefault("0")
    @Column(name = "ql_volume_scan")
    private int qlVolumeScan;

    @ColumnDefault("0")
    @Column(name = "time_volume_scan")
    private double timeVolumeScan;

    @ColumnDefault("0")
    @Column(name = "ql_return_sort")
    private int qlReturnSort;

    @ColumnDefault("0")
    @Column(name = "time_return_sort")
    private double timeReturnSort;

    @ColumnDefault("0")
    @Column(name = "ql_stocktaking")
    private int qlStocktaking;

    @ColumnDefault("0")
    @Column(name = "time_stocktaking")
    private double timeStocktaking;

    @ColumnDefault("0")
    @Column(name = "pick_nos_1")
    private int pickNos1;

    @ColumnDefault("0")
    @Column(name = "pick_nos_2")
    private int pickNos2;

    @ColumnDefault("0")
    @Column(name = "stow_clarifications")
    private int stowClarifications;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", referencedColumnName = "id")
    private ShiftTimeWork shiftId;
}