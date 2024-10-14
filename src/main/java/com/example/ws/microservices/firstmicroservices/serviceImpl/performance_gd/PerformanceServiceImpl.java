package com.example.ws.microservices.firstmicroservices.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.*;
import com.example.ws.microservices.firstmicroservices.repository.performance_gd.PerformanceRepository;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.ActivityNameService;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.FinalClusterService;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.PerformanceService;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.SpiClusterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class PerformanceServiceImpl implements PerformanceService {

    private PerformanceRepository performanceRepository;
    private ActivityNameService activityNameService;
    private FinalClusterService finalClusterService;
    private SpiClusterService spiClusterService;

    @Override
    @Transactional
    public void processFile(List<List<String>> allLineFiles, Map<String, String> checkHeaderList, List<String> headersName) {
        Map<String, Integer> indexMap = IntStream.range(0, headersName.size())
                .boxed()
                .collect(Collectors.toMap(
                        index -> checkHeaderList.get(headersName.get(index)),
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
                    throw new RuntimeException("QWe");
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

        List<Performance> allPerformanceToSave = allLineFiles.stream().map(eachProcessLine -> {
            Performance performance = new Performance();
            performance.setExpertis(eachProcessLine.get(indexMap.get(checkHeaderList.get("expetis"))));
            performance.setActivityName(allActivityNames.stream().filter(eachElement -> eachElement.getName().equals(eachProcessLine.get(indexMap.get(checkHeaderList.get("activityName"))))).findFirst().get());
            performance.setFinalCluster(allFinalClusters.stream().filter(eachElement -> eachElement.getName().equals(eachProcessLine.get(indexMap.get(checkHeaderList.get("finalCluster"))))).findFirst().get());
            performance.setActivityCluster(allSpiClusters.stream().filter(eachElement -> eachElement.getName().equals(eachProcessLine.get(indexMap.get(checkHeaderList.get("category"))))).findFirst().get());
            performance.setStartActivity(parseDate(eachProcessLine.get(indexMap.get(checkHeaderList.get("startActivity")))));
            performance.setEndActivity(parseDate(eachProcessLine.get(indexMap.get(checkHeaderList.get("endActivity")))));
            performance.setDuration(safeParseBigDecimal(eachProcessLine.get(indexMap.get(checkHeaderList.get("duration")))));
            performance.setQlBox(safeParseShort(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlBox")))));
            performance.setQlHanging(safeParseShort(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlHanging")))));
            performance.setQlShoes(safeParseShort(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlShoes")))));
            performance.setQlBoots(safeParseShort(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlBoots")))));
            performance.setQlOther(safeParseShort(eachProcessLine.get(indexMap.get(checkHeaderList.get("qlOther")))));
            performance.setStowClarifications(safeParseShort(eachProcessLine.get(indexMap.get(checkHeaderList.get("stowClarifications")))));
            performance.setPickNos1(safeParseShort(eachProcessLine.get(indexMap.get(checkHeaderList.get("pickNos1")))));
            performance.setPickNos2(safeParseShort(eachProcessLine.get(indexMap.get(checkHeaderList.get("pickNos2")))));

            performance.setQl(
                    Stream.of("ql", "qlReturn", "sortReturn", "qlWmo", "cartrunner", "relocation", "stocktaking", "volumescan")
                            .map(indexMap::get)
                            .map(eachIndex -> {
                                try {
                                    return Integer.parseInt(eachProcessLine.get(eachIndex));
                                } catch (NumberFormatException e) {
                                    return 0;
                                }
                            })
                            .reduce(0, Integer::sum).shortValue()
            );

            LocalDate date = LocalDate.parse(eachProcessLine.get(indexMap.get(checkHeaderList.get("date"))), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            performance.setPerformanceId(new PerformanceId(null, date));

            return performance;
        }).toList();

        performanceRepository.saveAll(allPerformanceToSave);
    }

    private Instant parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        return zonedDateTime.toInstant();
    }

    private Short safeParseShort(String str) {
        try {
            return Short.parseShort(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private BigDecimal safeParseBigDecimal(String str) {
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

}
