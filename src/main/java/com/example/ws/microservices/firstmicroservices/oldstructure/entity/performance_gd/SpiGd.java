package com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "spi_gd", schema = "performance_gd")
public class SpiGd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('performance_dg.spi_gd_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ColumnDefault("0")
    @Column(name = "spi_i_core")
    private Double spiICore;

    @ColumnDefault("0")
    @Column(name = "spi_i_core_time")
    private Double spiICoreTime;

    @ColumnDefault("0")
    @Column(name = "spi_ii_core")
    private Double spiIiCore;

    @ColumnDefault("0")
    @Column(name = "spi_ii_core_time")
    private Double spiIiCoreTime;

    @ColumnDefault("0")
    @Column(name = "spi_iii_core")
    private Double spiIiiCore;

    @ColumnDefault("0")
    @Column(name = "spi_iii_core_time")
    private Double spiIiiCoreTime;

    @ColumnDefault("0")
    @Column(name = "core_total_ql")
    private Integer coreTotalQl;

    @ColumnDefault("0")
    @Column(name = "core_total_time")
    private Double coreTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_repack")
    private Double spiIRepack;

    @ColumnDefault("0")
    @Column(name = "spi_iii_repack")
    private Double spiIiiRepack;

    @ColumnDefault("0")
    @Column(name = "repack_total_ql")
    private Integer repackTotalQl;

    @ColumnDefault("0")
    @Column(name = "repack_total_time")
    private Double repackTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_fast")
    private Double spiIFast;

    @ColumnDefault("0")
    @Column(name = "spi_ii_fast")
    private Double spiIiFast;

    @ColumnDefault("0")
    @Column(name = "spi_iii_fast")
    private Double spiIiiFast;

    @ColumnDefault("0")
    @Column(name = "fast_total_ql")
    private Integer fastTotalQl;

    @ColumnDefault("0")
    @Column(name = "fast_total_time")
    private Double fastTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_ii_goods")
    private Double spiIiGoods;

    @ColumnDefault("0")
    @Column(name = "spi_iii_goods")
    private Double spiIiiGoods;

    @ColumnDefault("0")
    @Column(name = "goods_total_ql")
    private Integer goodsTotalQl;

    @ColumnDefault("0")
    @Column(name = "goods_total_time")
    private Double goodsTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_ii_shipping")
    private Double spiIiShipping;

    @ColumnDefault("0")
    @Column(name = "spi_iii_shipping")
    private Double spiIiiShipping;

    @ColumnDefault("0")
    @Column(name = "shipping_total_ql")
    private Integer shippingTotalQl;

    @ColumnDefault("0")
    @Column(name = "shipping_total_time")
    private Double shippingTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_stow")
    private Double spiIStow;

    @ColumnDefault("0")
    @Column(name = "spi_ii_stow")
    private Double spiIiStow;

    @ColumnDefault("0")
    @Column(name = "spi_iii_stow")
    private Double spiIiiStow;

    @ColumnDefault("0")
    @Column(name = "stow_total_ql")
    private Integer stowTotalQl;

    @ColumnDefault("0")
    @Column(name = "stow_total_time")
    private Double stowTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_pick")
    private Double spiIPick;

    @ColumnDefault("0")
    @Column(name = "spi_ii_pick")
    private Double spiIiPick;

    @ColumnDefault("0")
    @Column(name = "spi_iii_pick")
    private Double spiIiiPick;

    @ColumnDefault("0")
    @Column(name = "pick_total_ql")
    private Integer pickTotalQl;

    @ColumnDefault("0")
    @Column(name = "pick_total_time")
    private Double pickTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_sort")
    private Double spiISort;

    @ColumnDefault("0")
    @Column(name = "spi_ii_sort")
    private Double spiIiSort;

    @ColumnDefault("0")
    @Column(name = "spi_iii_sort")
    private Double spiIiiSort;

    @ColumnDefault("0")
    @Column(name = "sort_total_ql")
    private Integer sortTotalQl;

    @ColumnDefault("0")
    @Column(name = "sort_total_time")
    private Double sortTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_single")
    private Double spiISingle;

    @ColumnDefault("0")
    @Column(name = "spi_ii_single")
    private Double spiIiSingle;

    @ColumnDefault("0")
    @Column(name = "spi_iii_single")
    private Double spiIiiSingle;

    @ColumnDefault("0")
    @Column(name = "single_total_ql")
    private Integer singleTotalQl;

    @ColumnDefault("0")
    @Column(name = "single_total_time")
    private Double singleTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_multi")
    private Double spiIMulti;

    @ColumnDefault("0")
    @Column(name = "spi_ii_multi")
    private Double spiIiMulti;

    @ColumnDefault("0")
    @Column(name = "spi_iii_multi")
    private Double spiIiiMulti;

    @ColumnDefault("0")
    @Column(name = "multi_total_ql")
    private Integer multiTotalQl;

    @ColumnDefault("0")
    @Column(name = "multi_total_time")
    private Double multiTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_opt")
    private Double spiIOpt;

    @ColumnDefault("0")
    @Column(name = "spi_ii_opt")
    private Double spiIiOpt;

    @ColumnDefault("0")
    @Column(name = "spi_iii_opt")
    private Double spiIiiOpt;

    @ColumnDefault("0")
    @Column(name = "opt_total_ql")
    private Integer optTotalQl;

    @ColumnDefault("0")
    @Column(name = "opt_total_time")
    private Double optTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_wmo")
    private Double spiIWmo;

    @ColumnDefault("0")
    @Column(name = "spi_ii_wmo")
    private Double spiIiWmo;

    @ColumnDefault("0")
    @Column(name = "spi_iii_wmo")
    private Double spiIiiWmo;

    @ColumnDefault("0")
    @Column(name = "wmo_total_ql")
    private Integer wmoTotalQl;

    @ColumnDefault("0")
    @Column(name = "wmo_total_time")
    private Double wmoTotalTime;

    @ColumnDefault("0")
    @Column(name = "spi_i_return")
    private Double spiIReturn;

    @ColumnDefault("0")
    @Column(name = "spi_ii_return")
    private Double spiIiReturn;

    @ColumnDefault("0")
    @Column(name = "spi_iii_return")
    private Double spiIiiReturn;

    @ColumnDefault("0")
    @Column(name = "return_total_ql")
    private Integer returnTotalQl;

    @ColumnDefault("0")
    @Column(name = "return_total_time")
    private Double returnTotalTime;

}