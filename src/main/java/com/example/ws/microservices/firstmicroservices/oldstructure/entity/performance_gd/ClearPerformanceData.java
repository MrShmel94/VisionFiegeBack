package com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.attendance.ShiftTimeWork;

import java.time.LocalDate;

public interface ClearPerformanceData {
    LocalDate getDate();

    String getExpertis();

    ShiftTimeWork getShiftId();

    double getTimeOpt();

    double getTimeMulti();

    double getTimeSingle();

    double getTimeSort();

    double getTimeWmo();

    double getTimeNco();

    double getTimePick();

    double getTimeStow();

    double getTimeReturn();

    double getTimeFast();

    double getTimeRepack();

    double getTimeRepl();

    double getTimeCore();

    double getNttOpt();

    double getNttMulti();

    double getNttSingle();

    double getNttPack();

    double getNttSort();

    double getNttWmo();

    double getNttNco();

    double getNttStow();

    double getNttPick();

    double getNttShipping();

    double getNttReturn();

    double getNttFast();

    double getNttCore();

    double getNttGoods();

    double getSupportOpt();

    double getSupportPack();

    double getSupportSort();

    double getSupportWmo();

    double getSupportNco();

    double getSupportStow();

    double getSupportPick();

    double getSupportShipping();

    double getSupportReturn();

    double getSupportRepack();

    double getSupportCore();

    double getSupportGoods();

    double getDurationIdle();

    int getIdleCount();

    int getQlRelocation();

    double getTimeRelocation();

    int getQlVolumeScan();

    double getTimeVolumeScan();

    int getQlReturnSort();

    double getTimeReturnSort();

    int getQlStocktaking();

    double getTimeStocktaking();

    int getQlOpt();

    int getQlMulti();

    int getQlSingle();

    int getQlSort();

    int getQlWmo();

    int getQlNco();

    int getQlStow();

    int getQlPick();

    int getQlReturn();

    int getQlFast();

    int getQlRepack();

    int getQlRepl();

    int getQlCore();

    int getQlCoreBoxed();

    int getQlCoreHanging();

    int getQlCoreShoes();

    int getQlCoreBoots();

    int getQlCoreOther();

    int getPickNos1();

    int getPickNos2();

    int getStowClarifications();
}
