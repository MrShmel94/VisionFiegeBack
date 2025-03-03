package com.example.ws.microservices.firstmicroservices.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.dto.performance.gd.PerformanceRowDTO;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeMapping;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.*;
import com.example.ws.microservices.firstmicroservices.mapper.EmployeeMapper;
import com.example.ws.microservices.firstmicroservices.mapper.PerformanceMapper;
import com.example.ws.microservices.firstmicroservices.repository.performance_gd.PerformanceRowDTOJdbcRepository;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class PerformanceServiceImpl implements PerformanceService {

    private ActivityNameService activityNameService;
    private FinalClusterService finalClusterService;
    private SpiClusterService spiClusterService;
    private final PerformanceRowDTOJdbcRepository performanceRowDTOJdbcRepository;
    private final ClearPerformanceEmployeeService clearPerformanceEmployeeService;
    private final SpiGdService spiGdService;

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<PerformanceRowDTO> getPerformanceData(LocalDate start, LocalDate end) {
        return performanceRowDTOJdbcRepository.getAllByDateBetween(start, end);
    }

    @Override
    public void processFile(Connection conn, List<List<String>> allLineFiles, Map<String, String> checkHeaderList, List<String> headersName) {

        Map<String, Integer> indexMap = IntStream.range(0, headersName.size())
                .boxed()
                .collect(Collectors.toMap(
                        headersName::get,
                        index -> index
                ));

        List<SpiCluster> allSpiClusters = spiClusterService.getAllSpiClusters();
        List<String> allNamingSpiClusters = allSpiClusters.stream().map(SpiCluster::getNameTable).toList();

        List<FinalCluster> allFinalClusters = finalClusterService.getAllFinalClusters();
        List<String> allNamingFinalClusters = allFinalClusters.stream().map(FinalCluster::getName).toList();

        List<ActivityName> allActivityNames = activityNameService.getAllActivityNames();
        List<String> allNamingActivityNames = allActivityNames.stream().map(ActivityName::getName).toList();

        int indexActivityName = headersName.indexOf(checkHeaderList.get("activityName"));
        int indexSpiClusterName = headersName.indexOf(checkHeaderList.get("category"));
        int indexFinalClusterName = headersName.indexOf(checkHeaderList.get("finalCluster"));

        Set<String> activityNames = new HashSet<>();
        Set<String> spiClusterNames = new HashSet<>();
        Set<String> finalClusterNames = new HashSet<>();

        Map<String, String> clusterActivityPerSPI = new HashMap<>();

        allLineFiles.forEach(row -> {
            String activityName = row.get(indexActivityName);
            String spiClusterName = row.get(indexSpiClusterName);

            activityNames.add(activityName);
            spiClusterNames.add(spiClusterName);
            finalClusterNames.add(row.get(indexFinalClusterName));

            if(!clusterActivityPerSPI.containsKey(activityName)){
                clusterActivityPerSPI.put(activityName, spiClusterName);
            }else{
                if(!clusterActivityPerSPI.get(activityName).equals(spiClusterName)){
                    throw new RuntimeException("Activity belongs to different SPI clusters");
                }
            }
        });

        List<String> newSpiClusters = spiClusterNames.stream()
                .filter(name -> !allNamingSpiClusters.contains(name))
                .toList();
        if (!newSpiClusters.isEmpty()) {
            List<SpiCluster> addedSpiClusters = newSpiClusters.stream()
                    .map(name -> {
                        SpiCluster newSpiCluster = new SpiCluster();
                        newSpiCluster.setNameTable(name);
                        newSpiCluster.setName("unknown");
                        return newSpiCluster;
                    })
                    .toList();

            spiClusterService.saveAll(addedSpiClusters);
            allSpiClusters.addAll(addedSpiClusters);
            log.info("Added new SpiClusters: {}", newSpiClusters);
        }

        List<String> newActivityNames = activityNames.stream()
                .filter(name -> !allNamingActivityNames.contains(name))
                .toList();
        if (!newActivityNames.isEmpty()) {
            List<ActivityName> addedActivityNames = newActivityNames.stream()
                    .map(name -> {
                        ActivityName newActivityName = new ActivityName();
                        newActivityName.setName(name);
                        newActivityName.setSpiCluster(allSpiClusters.stream().filter(eachCluster -> eachCluster.getNameTable().equals(clusterActivityPerSPI.get(name))).findFirst().get());
                        return newActivityName;
                    })
                    .toList();

            activityNameService.saveAll(addedActivityNames);
            allActivityNames.addAll(addedActivityNames);
            log.info("Added new ActivityNames: {}", newActivityNames);
        }

        List<String> newFinalClustersName = finalClusterNames.stream()
                .filter(name -> !allNamingFinalClusters.contains(name))
                .toList();
        if (!newFinalClustersName.isEmpty()) {
            List<FinalCluster> addedFinalClusters = newFinalClustersName.stream()
                    .map(name -> {
                        FinalCluster newFinalCluster = new FinalCluster();
                        newFinalCluster.setName(name);
                        return newFinalCluster;
                    })
                    .toList();

            finalClusterService.saveAll(addedFinalClusters);
            allFinalClusters.addAll(addedFinalClusters);
            log.info("Added new FinalClusters: {}", newFinalClustersName);
        }

        List<PerformanceRow> allPerformanceToSave = IntStream.range(0, allLineFiles.size())
                                                          .mapToObj(index -> {
                                                              List<String> eachProcessLine = allLineFiles.get(index);
            PerformanceRow performance = new PerformanceRow();
            performance.setExpertis(eachProcessLine.get(indexMap.get(checkHeaderList.get("expertis"))).replace(".0", ""));
            performance.setActivityName(allActivityNames.stream().filter(eachElement -> eachElement.getName().equals(eachProcessLine.get(indexMap.get(checkHeaderList.get("activityName"))))).findFirst().orElseThrow());
            performance.setFinalCluster(allFinalClusters.stream().filter(eachElement -> eachElement.getName().equals(eachProcessLine.get(indexMap.get(checkHeaderList.get("finalCluster"))))).findFirst().orElseThrow());
            performance.setActivityCluster(allSpiClusters.stream().filter(eachElement -> eachElement.getNameTable().equals(eachProcessLine.get(indexMap.get(checkHeaderList.get("category"))))).findFirst().orElseThrow());
            performance.setStartActivity(parseDate(eachProcessLine.get(indexMap.get(checkHeaderList.get("startActivity")))));
            performance.setEndActivity(parseDate(eachProcessLine.get(indexMap.get(checkHeaderList.get("endActivity")))));
            performance.setDuration(safeParseDouble(eachProcessLine.get(indexMap.get(checkHeaderList.get("duration")))));
            performance.setQlBox(safeParseInteger(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlBox")))));
            performance.setQlHanging(safeParseInteger(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlHanging")))));
            performance.setQlShoes(safeParseInteger(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlShoes")))));
            performance.setQlBoots(safeParseInteger(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlBoots")))));
            performance.setQlOther(safeParseInteger(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlOther")))));
            performance.setStowClarifications(safeParseInteger(eachProcessLine.get(indexMap.get(checkHeaderList.get("stowClarifications")))));
            performance.setPickNos1(safeParseInteger(eachProcessLine.get(indexMap.get(checkHeaderList.get("pickNos1")))));
            performance.setPickNos2(safeParseInteger(eachProcessLine.get(indexMap.get(checkHeaderList.get("pickNos2")))));

            performance.setQl(
                    Stream.of("ql", "qlReturn", "sortReturn", "qlWmo", "cartrunner", "relocation", "stocktaking", "volumescan")
                            .map(checkHeaderList::get)
                            .map(indexMap::get)
                            .map(eachIndex -> {
                                try {
                                    return (int) Double.parseDouble(eachProcessLine.get(eachIndex));
                                } catch (NumberFormatException e) {
                                    return 0;
                                }
                            })
                            .reduce(0, Integer::sum)
            );

            LocalDate date = parseDateToLocalDate(eachProcessLine.get(indexMap.get(checkHeaderList.get("date"))));
            performance.setDate(date);

            return performance;
        }).toList();

        if (allPerformanceToSave.isEmpty()) {
            log.info("No lines to process, skipping insertion.");
            return;
        }

        LocalDate partitionDate = allPerformanceToSave.get(0).getDate();

        ensurePartitionExistsWithConnection(conn, partitionDate);
        copyInsertPerformance(conn, allPerformanceToSave);



        Map<String, List<PerformanceRowDTO>> mapAllPerformance = allPerformanceToSave
                .stream()
                .map(PerformanceMapper.INSTANCE::toPerformanceRowDto)
                .collect(Collectors.groupingBy(PerformanceRowDTO::getExpertis));

        processingClearPerformance(mapAllPerformance);
    }

    private void processingClearPerformance(Map<String, List<PerformanceRowDTO>> mapAllPerformance) {
        SpiGd spiGd = new SpiGd();
        double totalTimePack = 0d;
        Map<String, ClearPerformanceEmployee> clearPerformance = new HashMap<>();
        for(Entry<String, List<PerformanceRowDTO>> entry : mapAllPerformance.entrySet()){
            ClearPerformanceEmployee clearPerformanceEmployee = new ClearPerformanceEmployee();
            clearPerformanceEmployee.setDate(entry.getValue().getFirst().getDate());
            clearPerformanceEmployee.setExpertis(entry.getValue().getFirst().getExpertis());

            for (PerformanceRowDTO eachObj : entry.getValue()) {
                if (eachObj.getActivityName().equalsIgnoreCase("LINESORTPACK")) {
                    clearPerformanceEmployee.setQlOpt(clearPerformanceEmployee.getQlOpt() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeOpt(clearPerformanceEmployee.getTimeOpt() + eachObj.getDuration());

                    spiGd.setOptTotalQl(spiGd.getOptTotalQl() + eachObj.getQl());
                    spiGd.setShippingTotalQl(spiGd.getShippingTotalQl() + eachObj.getQl());
                    spiGd.setOptTotalTime(spiGd.getOptTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("PACK_MULTI")) {
                    clearPerformanceEmployee.setQlMulti(clearPerformanceEmployee.getQlMulti() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeMulti(clearPerformanceEmployee.getTimeMulti() + eachObj.getDuration());

                    spiGd.setMultiTotalQl(spiGd.getMultiTotalQl() + eachObj.getQl());
                    spiGd.setShippingTotalQl(spiGd.getShippingTotalQl() + eachObj.getQl());
                    spiGd.setMultiTotalTime(spiGd.getMultiTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("PACK_SINGLE")) {
                    clearPerformanceEmployee.setQlSingle(clearPerformanceEmployee.getQlSingle() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeSingle(clearPerformanceEmployee.getTimeSingle() + eachObj.getDuration());

                    spiGd.setSingleTotalQl(spiGd.getSingleTotalQl() + eachObj.getQl());
                    spiGd.setShippingTotalQl(spiGd.getShippingTotalQl() + eachObj.getQl());
                    spiGd.setSingleTotalTime(spiGd.getSingleTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("SORT")) {
                    clearPerformanceEmployee.setQlSort(clearPerformanceEmployee.getQlSort() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeSort(clearPerformanceEmployee.getTimeSort() + eachObj.getDuration());

                    spiGd.setSortTotalQl(spiGd.getSortTotalQl() + eachObj.getQl());
                    spiGd.setSortTotalTime(spiGd.getSortTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("WMO_Outbound_Repacking")) {
                    clearPerformanceEmployee.setQlWmo(clearPerformanceEmployee.getQlWmo() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeWmo(clearPerformanceEmployee.getTimeWmo() + eachObj.getDuration());

                    spiGd.setWmoTotalQl(spiGd.getWmoTotalQl() + eachObj.getQl());
                    spiGd.setWmoTotalTime(spiGd.getWmoTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("NCO PACK direct")) {
                    clearPerformanceEmployee.setQlNco(clearPerformanceEmployee.getQlNco() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeNco(clearPerformanceEmployee.getTimeNco() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("PICK")) {
                    clearPerformanceEmployee.setQlPick(clearPerformanceEmployee.getQlPick() + eachObj.getQl());
                    clearPerformanceEmployee.setTimePick(clearPerformanceEmployee.getTimePick() + eachObj.getDuration());

                    spiGd.setPickTotalQl(spiGd.getPickTotalQl() + eachObj.getQl());
                    spiGd.setPickTotalTime(spiGd.getPickTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("STOW")) {
                    clearPerformanceEmployee.setQlStow(clearPerformanceEmployee.getQlStow() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeStow(clearPerformanceEmployee.getTimeStow() + eachObj.getDuration());

                    spiGd.setStowTotalQl(spiGd.getStowTotalQl() + eachObj.getQl());
                    spiGd.setStowTotalTime(spiGd.getStowTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("DEFECT ITEM - CATEGORIZATION")) {
                    clearPerformanceEmployee.setQlReturn(clearPerformanceEmployee.getQlReturn() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeReturn(clearPerformanceEmployee.getTimeReturn() + eachObj.getDuration());

                    spiGd.setReturnTotalQl(spiGd.getReturnTotalQl() + eachObj.getQl());
                    spiGd.setReturnTotalTime(spiGd.getReturnTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("FASTLANE RECEIVE")) {
                    clearPerformanceEmployee.setQlFast(clearPerformanceEmployee.getQlFast() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeFast(clearPerformanceEmployee.getTimeFast() + eachObj.getDuration());

                    spiGd.setFastTotalQl(spiGd.getFastTotalQl() + eachObj.getQl());
                    spiGd.setGoodsTotalQl(spiGd.getGoodsTotalQl() + eachObj.getQl());
                    spiGd.setFastTotalTime(spiGd.getFastTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("REPACKRECEIVE")) {
                    clearPerformanceEmployee.setQlRepack(clearPerformanceEmployee.getQlRepack() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeRepack(clearPerformanceEmployee.getTimeRepack() + eachObj.getDuration());

                    spiGd.setOptTotalQl(spiGd.getOptTotalQl() + eachObj.getQl());
                    spiGd.setGoodsTotalQl(spiGd.getGoodsTotalQl() + eachObj.getQl());
                    spiGd.setOptTotalTime(spiGd.getOptTotalTime() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("REREPLENISHMENT")) {
                    clearPerformanceEmployee.setQlRepl(clearPerformanceEmployee.getQlRepl() + eachObj.getQl());
                    clearPerformanceEmployee.setTimeRepl(clearPerformanceEmployee.getTimeRepl() + eachObj.getDuration());
                } else if (eachObj.getActivityName().equalsIgnoreCase("RECEIVE")) {
                    clearPerformanceEmployee.setQlCore(clearPerformanceEmployee.getQlCore() + eachObj.getQl());
                    clearPerformanceEmployee.setQlCoreBoxed(clearPerformanceEmployee.getQlCoreBoxed() + eachObj.getQlBox());
                    clearPerformanceEmployee.setQlCoreHanging(clearPerformanceEmployee.getQlCoreHanging() + eachObj.getQlHanging());
                    clearPerformanceEmployee.setQlCoreShoes(clearPerformanceEmployee.getQlCoreShoes() + eachObj.getQlShoes());
                    clearPerformanceEmployee.setQlCoreBoots(clearPerformanceEmployee.getQlCoreBoots() + eachObj.getQlBoots());
                    clearPerformanceEmployee.setQlCoreOther(clearPerformanceEmployee.getQlCoreOther() + eachObj.getQlOther());
                    clearPerformanceEmployee.setTimeCore(clearPerformanceEmployee.getTimeCore() + eachObj.getDuration());

                    spiGd.setCoreTotalQl(spiGd.getCoreTotalQl() + eachObj.getQl());
                    spiGd.setGoodsTotalQl(spiGd.getGoodsTotalQl() + eachObj.getQl());
                    spiGd.setCoreTotalTime(spiGd.getCoreTotalTime() + eachObj.getDuration());
                    spiGd.setSpiICoreTime(spiGd.getSpiICoreTime() + eachObj.getDuration());
                }

                if (eachObj.getActivityCluster().equalsIgnoreCase("spi_II")) {
                    if (eachObj.getFinalCluster().equalsIgnoreCase("Linesorter Pack")) {
                        clearPerformanceEmployee.setNttOpt(clearPerformanceEmployee.getNttOpt() + eachObj.getDuration());

                        spiGd.setOptTotalTime(spiGd.getOptTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Standard Pack Multi")) {
                        clearPerformanceEmployee.setNttMulti(clearPerformanceEmployee.getNttMulti() + eachObj.getDuration());

                        spiGd.setMultiTotalTime(spiGd.getMultiTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Standard Pack Single")) {
                        clearPerformanceEmployee.setNttSingle(clearPerformanceEmployee.getNttSingle() + eachObj.getDuration());

                        spiGd.setSingleTotalTime(spiGd.getSingleTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Pack")) {
                        clearPerformanceEmployee.setNttPack(clearPerformanceEmployee.getNttPack() + eachObj.getDuration());

                        totalTimePack += eachObj.getDuration();
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Manual Sort")) {
                        clearPerformanceEmployee.setNttSort(clearPerformanceEmployee.getNttSort() + eachObj.getDuration());

                        spiGd.setSortTotalTime(spiGd.getSortTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Internal Orders Shipping")) {
                        clearPerformanceEmployee.setNttWmo(clearPerformanceEmployee.getNttWmo() + eachObj.getDuration());

                        spiGd.setWmoTotalTime(spiGd.getWmoTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("NCO Shipping")) {
                        clearPerformanceEmployee.setNttNco(clearPerformanceEmployee.getNttNco() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Core Stow")) {
                        clearPerformanceEmployee.setNttStow(clearPerformanceEmployee.getNttStow() + eachObj.getDuration());

                        spiGd.setStowTotalTime(spiGd.getStowTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Pick")) {
                        clearPerformanceEmployee.setNttPick(clearPerformanceEmployee.getNttPick() + eachObj.getDuration());

                        spiGd.setPickTotalTime(spiGd.getPickTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Shipping")) {
                        clearPerformanceEmployee.setNttShipping(clearPerformanceEmployee.getNttShipping() + eachObj.getDuration());

                        spiGd.setShippingTotalTime(spiGd.getShippingTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Core Retoure") || eachObj.getFinalCluster().equalsIgnoreCase("Refurbishment")) {
                        clearPerformanceEmployee.setNttReturn(clearPerformanceEmployee.getNttReturn() + eachObj.getDuration());

                        spiGd.setReturnTotalTime(spiGd.getReturnTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Fastlane Receive")) {
                        clearPerformanceEmployee.setNttFast(clearPerformanceEmployee.getNttFast() + eachObj.getDuration());

                        spiGd.setFastTotalTime(spiGd.getFastTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Core Receive")) {
                        clearPerformanceEmployee.setNttCore(clearPerformanceEmployee.getNttCore() + eachObj.getDuration());

                        spiGd.setCoreTotalTime(spiGd.getCoreTotalTime() + eachObj.getDuration());
                        spiGd.setSpiIiCoreTime(spiGd.getSpiIiCoreTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Goodsreceive")) {
                        clearPerformanceEmployee.setNttGoods(clearPerformanceEmployee.getNttGoods() + eachObj.getDuration());

                        spiGd.setGoodsTotalTime(spiGd.getGoodsTotalTime() + eachObj.getDuration());
                    }
                } else if (eachObj.getActivityCluster().equalsIgnoreCase("spi_III")) {
                    if (eachObj.getFinalCluster().equalsIgnoreCase("Linesorter Pack")) {
                        clearPerformanceEmployee.setSupportOpt(clearPerformanceEmployee.getSupportOpt() + eachObj.getDuration());

                        spiGd.setOptTotalTime(spiGd.getOptTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Pack")) {
                        clearPerformanceEmployee.setSupportPack(clearPerformanceEmployee.getSupportPack() + eachObj.getDuration());

                        totalTimePack += eachObj.getDuration();
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Manual Sort")) {
                        clearPerformanceEmployee.setSupportSort(clearPerformanceEmployee.getSupportSort() + eachObj.getDuration());

                        spiGd.setSortTotalTime(spiGd.getSortTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Internal Orders Shipping")) {
                        clearPerformanceEmployee.setSupportWmo(clearPerformanceEmployee.getSupportWmo() + eachObj.getDuration());

                        spiGd.setWmoTotalTime(spiGd.getWmoTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("NCO Shipping")) {
                        clearPerformanceEmployee.setSupportNco(clearPerformanceEmployee.getSupportNco() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Core Stow")) {
                        clearPerformanceEmployee.setSupportStow(clearPerformanceEmployee.getSupportStow() + eachObj.getDuration());

                        spiGd.setStowTotalTime(spiGd.getStowTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Pick")) {
                        clearPerformanceEmployee.setSupportPick(clearPerformanceEmployee.getSupportPick() + eachObj.getDuration());

                        spiGd.setPickTotalTime(spiGd.getPickTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Shipping")) {
                        clearPerformanceEmployee.setSupportShipping(clearPerformanceEmployee.getSupportShipping() + eachObj.getDuration());

                        spiGd.setShippingTotalTime(spiGd.getShippingTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Refurbishment")) {
                        clearPerformanceEmployee.setSupportReturn(clearPerformanceEmployee.getSupportReturn() + eachObj.getDuration());

                        spiGd.setReturnTotalTime(spiGd.getReturnTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Repackreceive")) {
                        clearPerformanceEmployee.setSupportRepack(clearPerformanceEmployee.getSupportRepack() + eachObj.getDuration());

                        spiGd.setRepackTotalTime(spiGd.getRepackTotalTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Core Receive")) {
                        clearPerformanceEmployee.setSupportCore(clearPerformanceEmployee.getSupportCore() + eachObj.getDuration());

                        spiGd.setCoreTotalTime(spiGd.getCoreTotalTime() + eachObj.getDuration());
                        spiGd.setSpiIiiCoreTime(spiGd.getSpiIiiCoreTime() + eachObj.getDuration());
                    } else if (eachObj.getFinalCluster().equalsIgnoreCase("Goodsreceive")) {
                        clearPerformanceEmployee.setSupportGoods(clearPerformanceEmployee.getSupportGoods() + eachObj.getDuration());

                        spiGd.setGoodsTotalTime(spiGd.getGoodsTotalTime() + eachObj.getDuration());
                    }
                }
            }

            clearPerformance.put(entry.getKey(), clearPerformanceEmployee);
        }

        double singleTimeAdditional = totalTimePack * spiGd.getSingleTotalQl() / (spiGd.getSingleTotalQl() + spiGd.getMultiTotalQl());

        spiGd.setSingleTotalTime(spiGd.getSingleTotalTime() + singleTimeAdditional);
        spiGd.setMultiTotalTime(spiGd.getMultiTotalTime() + (totalTimePack - singleTimeAdditional));



        clearPerformanceEmployeeService.saveAllPerformanceEmployee(clearPerformance.values().stream().toList());
        spiGdService.saveSpi(spiGd);
    }

    private void copyInsertPerformance(Connection conn,  List<PerformanceRow> list) {
        StringBuilder sb = new StringBuilder();

        log.info("Start building CSV: list.size = {}", list.size());
        log.info("Start processing StringBuilder : {}", LocalDateTime.now());

        for (PerformanceRow p : list) {
            sb.append(p.getDate() == null ? "" : p.getDate()).append(';');
            sb.append(escapeCsv(p.getExpertis())).append(';');
            sb.append(p.getActivityName().getId()).append(';');
            sb.append(p.getFinalCluster().getId()).append(';');
            sb.append(p.getActivityCluster().getId()).append(';');
            sb.append(formatTimestamp(p.getStartActivity())).append(';');
            sb.append(formatTimestamp(p.getEndActivity())).append(';');
            sb.append(p.getDuration()).append(';');
            sb.append(p.getQl()).append(';');
            sb.append(p.getQlBox()).append(';');
            sb.append(p.getQlHanging()).append(';');
            sb.append(p.getQlShoes()).append(';');
            sb.append(p.getQlBoots()).append(';');
            sb.append(p.getQlOther()).append(';');
            sb.append(p.getStowClarifications()).append(';');
            sb.append(p.getPickNos1()).append(';');
            sb.append(p.getPickNos2()).append('\n');
        }

        log.info("Finished building CSV. Total lines: {}", list.size());

        log.info("End processing StringBuilder : {}", LocalDateTime.now());

        byte[] csvData = sb.toString().getBytes();

        log.info("CSV data size in bytes: {}", csvData.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(csvData);

        try {
            PGConnection pgConn = conn.unwrap(org.postgresql.PGConnection.class);

            String copySql = """
                COPY performance_dg.performance (
                   date, expertis, activity_name_id, final_cluster_id, activity_cluster_id,
                   start_activity, end_activity, duration,
                   ql, ql_box, ql_hanging, ql_shoes, ql_boots, ql_other,
                   stow_clarifications, pick_nos1, pick_nos2
                )
                FROM STDIN
                DELIMITER ';'
                CSV
            """;

            log.info("Starting copyIn (expected columns: 17 per line) ... : {}", LocalDateTime.now());

            long rowCount = pgConn.getCopyAPI().copyIn(copySql, bais);
            log.info("COPY inserted {} rows into performance. : {}", rowCount, LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error COPY inserting performance data", e);
            throw new RuntimeException(e);
        }
    }

    private String escapeCsv(String text) {
        return (text == null) ? "" : text;
    }


    private String formatTimestamp(Instant inst) {
        if (inst == null) {
            return "";
        }
        return TIMESTAMP_FORMAT.format(inst.atZone(java.time.ZoneOffset.UTC));
    }

    private LocalDate parseDateToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        return zonedDateTime.toLocalDate();
    }

    private Instant parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        return zonedDateTime.toInstant();
    }

    private int safeParseInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private double safeParseDouble(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0.0;
        }

        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void ensurePartitionExistsWithConnection(Connection conn, LocalDate targetDate) {
        if (targetDate == null) return;

        LocalDate partitionStart = targetDate.withDayOfMonth(1);
        LocalDate partitionEnd = partitionStart.plusMonths(1);
        String partitionName = "performance_" + partitionStart.format(DateTimeFormatter.ofPattern("yyyy_MM"));

        log.info("Проверяем наличие partition {} для диапазона {} до {}", partitionName, partitionStart, partitionEnd);

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM information_schema.tables WHERE table_schema = 'performance_dg' AND table_name = ?")) {
            ps.setString(1, partitionName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    log.info("Partition {} не существует. Создаём...", partitionName);
                    try (Statement stmt = conn.createStatement()) {
                        String createPartitionSql = String.format(
                                "CREATE TABLE performance_dg.%s PARTITION OF performance_dg.performance " +
                                        "FOR VALUES FROM ('%s'::date) TO ('%s'::date)",
                                partitionName, partitionStart, partitionEnd
                        );
                        stmt.execute(createPartitionSql);
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_date ON performance_dg.%s (date)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_expertis ON performance_dg.%s (expertis)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_activity_name_id ON performance_dg.%s (activity_name_id)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_final_cluster_id ON performance_dg.%s (final_cluster_id)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_start_activity ON performance_dg.%s (start_activity)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_end_activity ON performance_dg.%s (end_activity)", partitionName, partitionName));
                    }
                    log.info("Partition {} успешно создан.", partitionName);
                } else {
                    log.info("Partition {} уже существует.", partitionName);
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка при создании partition", e);
            throw new RuntimeException(e);
        }
    }
}
