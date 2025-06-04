package com.example.ws.microservices.firstmicroservices.dto.performance.gd;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClearPerformanceGDDto {

    private LocalDate date;
    private String expertis;
    private int qlOpt;
    private double timeOpt;
    private double supportOpt;
    private double nttOpt;
    private int qlMulti;
    private double timeMulti;
    private double nttMulti;
    private int qlSingle;
    private double timeSingle;
    private double nttSingle;
    private double supportPack;
    private double nttPack;
    private int qlSort;
    private double timeSort;
    private double supportSort;
    private double nttSort;
    private int qlWmo;
    private double timeWmo;
    private double supportWmo;
    private double nttWmo;
    private int qlNco;
    private double timeNco;
    private double supportNco;
    private double nttNco;
    private int qlStow;
    private double timeStow;
    private double supportStow;
    private double nttStow;
    private int qlPick;
    private double timePick;
    private double supportPick;
    private double nttPick;
    private double supportShipping;
    private double nttShipping;
    private int qlReturn;
    private double timeReturn;
    private double supportReturn;
    private double nttReturn;
    private int qlFast;
    private double timeFast;
    private double nttFast;
    private int qlRepack;
    private double timeRepack;
    private double supportRepack;
    private int qlRepl;
    private double timeRepl;
    private int qlCore;
    private int qlCoreBoxed;
    private int qlCoreHanging;
    private int qlCoreShoes;
    private int qlCoreBoots;
    private int qlCoreOther;
    private double timeCore;
    private double supportCore;
    private double nttCore;
    private double supportGoods;
    private double nttGoods;

    private double durationIdle;
    private int idleCount;
    private int qlRelocation;
    private double timeRelocation;
    private int qlVolumeScan;
    private double timeVolumeScan;
    private int qlReturnSort;
    private double timeReturnSort;
    private int qlStocktaking;
    private double timeStocktaking;


    public ClearPerformanceGDDto mergeWith(ClearPerformanceGDDto other) {
        if (other == null) return this;

        this.qlOpt += other.qlOpt;
        this.timeOpt += other.timeOpt;
        this.supportOpt += other.supportOpt;
        this.nttOpt += other.nttOpt;

        this.qlMulti += other.qlMulti;
        this.timeMulti += other.timeMulti;
        this.nttMulti += other.nttMulti;

        this.qlSingle += other.qlSingle;
        this.timeSingle += other.timeSingle;
        this.nttSingle += other.nttSingle;

        this.supportPack += other.supportPack;
        this.nttPack += other.nttPack;

        this.qlSort += other.qlSort;
        this.timeSort += other.timeSort;
        this.supportSort += other.supportSort;
        this.nttSort += other.nttSort;

        this.qlWmo += other.qlWmo;
        this.timeWmo += other.timeWmo;
        this.supportWmo += other.supportWmo;
        this.nttWmo += other.nttWmo;

        this.qlNco += other.qlNco;
        this.timeNco += other.timeNco;
        this.supportNco += other.supportNco;
        this.nttNco += other.nttNco;

        this.qlStow += other.qlStow;
        this.timeStow += other.timeStow;
        this.supportStow += other.supportStow;
        this.nttStow += other.nttStow;

        this.qlPick += other.qlPick;
        this.timePick += other.timePick;
        this.supportPick += other.supportPick;
        this.nttPick += other.nttPick;

        this.supportShipping += other.supportShipping;
        this.nttShipping += other.nttShipping;

        this.qlReturn += other.qlReturn;
        this.timeReturn += other.timeReturn;
        this.supportReturn += other.supportReturn;
        this.nttReturn += other.nttReturn;

        this.qlFast += other.qlFast;
        this.timeFast += other.timeFast;
        this.nttFast += other.nttFast;

        this.qlRepack += other.qlRepack;
        this.timeRepack += other.timeRepack;
        this.supportRepack += other.supportRepack;

        this.qlRepl += other.qlRepl;
        this.timeRepl += other.timeRepl;

        this.qlCore += other.qlCore;
        this.qlCoreBoxed += other.qlCoreBoxed;
        this.qlCoreHanging += other.qlCoreHanging;
        this.qlCoreShoes += other.qlCoreShoes;
        this.qlCoreBoots += other.qlCoreBoots;
        this.qlCoreOther += other.qlCoreOther;
        this.timeCore += other.timeCore;
        this.supportCore += other.supportCore;
        this.nttCore += other.nttCore;

        this.supportGoods += other.supportGoods;
        this.nttGoods += other.nttGoods;

        this.durationIdle += other.durationIdle;
        this.idleCount += other.idleCount;
        this.qlRelocation += other.qlRelocation;
        this.qlVolumeScan += other.qlVolumeScan;
        this.qlReturnSort += other.qlReturnSort;
        this.qlStocktaking += other.qlStocktaking;

        this.timeRelocation += other.timeRelocation;
        this.timeVolumeScan += other.timeVolumeScan;
        this.timeReturnSort += other.timeReturnSort;
        this.timeStocktaking += other.timeStocktaking;

        return this;
    }
}
