package com.example.ws.microservices.firstmicroservices.dto.performance.gd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
