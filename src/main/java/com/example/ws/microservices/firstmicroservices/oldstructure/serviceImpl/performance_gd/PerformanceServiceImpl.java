package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.attendance.gd.ShiftTimeWorkDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd.PerformanceRowDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.attendance.ShiftTimeWork;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.*;
import com.example.ws.microservices.firstmicroservices.oldstructure.mapper.PerformanceMapper;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.ShiftTimeWorkService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceServiceImpl implements PerformanceService {

    private final ActivityNameService activityNameService;
    private final FinalClusterService finalClusterService;
    private final SpiClusterService spiClusterService;
    private final ShiftTimeWorkService shiftTimeWorkService;

    private final String SITE_NAME = "GD";
    private final Set<String> FILTERED_SHIFT = Set.of("I", "II", "III");
    private final int BATCH_SIZE = 500;

    private final SpiGdService spiGdService;

    @PersistenceContext
    private final EntityManager entityManager;

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);


    @Override
    public void processFile(Connection conn, List<List<String>> allLineFiles, Map<String, String> checkHeaderList, List<String> headersName) throws SQLException {

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

            if (!clusterActivityPerSPI.containsKey(activityName)) {
                clusterActivityPerSPI.put(activityName, spiClusterName);
            } else {
                if (!clusterActivityPerSPI.get(activityName).equals(spiClusterName)) {
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

        Map<String, ActivityName> activityNameMap = allActivityNames.stream()
                .collect(Collectors.toMap(ActivityName::getName, Function.identity()));

        Map<String, FinalCluster> finalClusterMap = allFinalClusters.stream()
                .collect(Collectors.toMap(FinalCluster::getName, Function.identity()));

        Map<String, SpiCluster> spiClusterMap = allSpiClusters.stream()
                .collect(Collectors.toMap(SpiCluster::getNameTable, Function.identity()));

        List<PerformanceRow> allPerformanceToSave = IntStream.range(0, allLineFiles.size())
                .mapToObj(index -> {
                    List<String> eachProcessLine = allLineFiles.get(index);
                    PerformanceRow performance = new PerformanceRow();
                    performance.setExpertis(eachProcessLine.get(indexMap.get(checkHeaderList.get("expertis"))).replace(".0", ""));

                    String activityNameKey = eachProcessLine.get(indexMap.get(checkHeaderList.get("activityName")));
                    ActivityName activityName = activityNameMap.get(activityNameKey);
                    if (activityName == null) {
                        throw new IllegalStateException("Unknown activity name: " + activityNameKey);
                    }
                    performance.setActivityName(activityName);

                    String finalClusterNameKey = eachProcessLine.get(indexMap.get(checkHeaderList.get("finalCluster")));
                    FinalCluster finalClusterName = finalClusterMap.get(finalClusterNameKey);
                    if (finalClusterName == null) {
                        throw new IllegalStateException("Unknown final cluster name: " + finalClusterNameKey);
                    }
                    performance.setFinalCluster(finalClusterName);

                    String spiClusterNameKey = eachProcessLine.get(indexMap.get(checkHeaderList.get("category")));
                    SpiCluster spiClusterName = spiClusterMap.get(spiClusterNameKey);
                    if (spiClusterName == null) {
                        throw new IllegalStateException("Unknown spi cluster name: " + spiClusterNameKey);
                    }
                    performance.setActivityCluster(spiClusterName);

                    performance.setStartActivity(parseDateTime(eachProcessLine.get(indexMap.get(checkHeaderList.get("startActivity")))));
                    performance.setEndActivity(parseDateTime(eachProcessLine.get(indexMap.get(checkHeaderList.get("endActivity")))));
                    performance.setDuration(safeParseDouble(eachProcessLine.get(indexMap.get(checkHeaderList.get("duration")))));
                    performance.setDurationIdle(safeParseDouble(eachProcessLine.get(indexMap.get(checkHeaderList.get("idleTime")))));
                    performance.setIdleCount((int)safeParseDouble(eachProcessLine.get(indexMap.get(checkHeaderList.get("idleCount")))));
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

        processingClearPerformance(conn, mapAllPerformance, activityNameMap);

        processingClearPerformanceWithTransferLogic(conn, mapAllPerformance);
    }

    public void processingClearPerformanceWithTransferLogic(Connection conn, Map<String, List<PerformanceRowDTO>> mapAllPerformance) throws SQLException {
        Map<String, ClearPerformanceEmployeeWithoutNttTatig> clearPerformance = new HashMap<>();

        Map<String, BiConsumer<ClearPerformanceEmployee, Double>> activityTimeOnlyLogic = buildActivityTimeOnlyLogicMap();
        Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>> activityLogic = buildActivityLogicMap();
        Map<String, Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>>> clusterLogic = buildClusterLogicMap();

        List<ShiftTimeWorkDTO> allShifts = shiftTimeWorkService.getShiftTimeWorkByNameSite(SITE_NAME).stream()
                .filter(dto -> FILTERED_SHIFT.contains(dto.getShiftCode()))
                .toList();

        for (Entry<String, List<PerformanceRowDTO>> entry : mapAllPerformance.entrySet()) {
            ClearPerformanceEmployee clearEmployee = new ClearPerformanceEmployee();
            List<PerformanceRowDTO> rows = entry.getValue();

            PerformanceRowDTO first = rows.getFirst();
            PerformanceRowDTO last = rows.getLast();

            LocalDateTime startActivityEmployee = first.getStartActivity();
            LocalDateTime endActivityEmployee = last.getEndActivity();

            if (!startActivityEmployee.toLocalDate().isEqual(endActivityEmployee.toLocalDate())) {
                Optional<ShiftTimeWorkDTO> nightShift = allShifts.stream()
                        .filter(dto -> dto.getShiftCode().equalsIgnoreCase("III"))
                        .findFirst();

                if (nightShift.isPresent()) {
                    clearEmployee.setShiftId(entityManager.getReference(ShiftTimeWork.class, nightShift.get().getShiftId()));
                } else {
                    throw new IllegalStateException("Not found Night Shift");
                }
            } else {
                ShiftTimeWorkDTO matchedShift = findBestMatchingShift(startActivityEmployee, endActivityEmployee, allShifts, first.getDate());
                if (matchedShift != null) {
                    clearEmployee.setShiftId(entityManager.getReference(ShiftTimeWork.class, matchedShift.getShiftId()));
                } else {
                    throw new IllegalStateException("Unknown shift for employee -> " + first.getExpertis());
                }
            }

            clearEmployee.setDate(first.getDate());
            clearEmployee.setExpertis(first.getExpertis());

            double idleTime = 0d;
            int idleCount = 0;

            for (int i = 0; i < rows.size(); i++) {
                PerformanceRowDTO row = rows.get(i);

                idleTime += row.getDurationIdle();
                idleCount += row.getIdleCount();

                if ("NTT Tätigkeitswechsel".equalsIgnoreCase(row.getActivityName()) && row.getDuration() <= 0.167
                        && i > 0 && i < rows.size() - 1) {

                    PerformanceRowDTO prev = rows.get(i - 1);
                    PerformanceRowDTO next = rows.get(i + 1);

                    if ("spi_i".equalsIgnoreCase(prev.getActivityCluster())
                            && "spi_i".equalsIgnoreCase(next.getActivityCluster())) {

                        String prevActivityKey = prev.getActivityName().toUpperCase();
                        BiConsumer<ClearPerformanceEmployee, Double> timeOnlyLogic = activityTimeOnlyLogic.get(prevActivityKey);
                        if (timeOnlyLogic != null) {
                            timeOnlyLogic.accept(clearEmployee, row.getDuration());
                        }

                        continue;
                    }
                }

                String activityKey = row.getActivityName().toUpperCase();
                BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO> actLogic = activityLogic.get(activityKey);
                if (actLogic != null) actLogic.accept(clearEmployee, row);

                String clusterKey = row.getActivityCluster().toUpperCase();
                String finalKey = row.getFinalCluster().toUpperCase();
                Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>> clusterMap = clusterLogic.get(clusterKey);
                if (clusterMap != null) {
                    BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO> finalLogic = clusterMap.get(finalKey);
                    if (finalLogic != null) finalLogic.accept(clearEmployee, row);
                }
            }

            clearEmployee.setIdleCount(idleCount);
            clearEmployee.setDurationIdle(idleTime);

            ClearPerformanceEmployeeWithoutNttTatig clone = new ClearPerformanceEmployeeWithoutNttTatig();
            BeanUtils.copyProperties(clearEmployee, clone);

            clearPerformance.put(entry.getKey(), clone);
        }

        insertClearPerformanceGeneric(conn, clearPerformance.values().stream().toList(), "clear_performance_employee_without_ntt_tatig");
    }

    private void processingClearPerformance(Connection conn, Map<String, List<PerformanceRowDTO>> mapAllPerformance, Map<String, ActivityName> activityNameMap) throws SQLException {
        Map<String, ClearPerformanceEmployee> clearPerformance = new HashMap<>();

        Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>> activityLogic = buildActivityLogicMap();
        Map<String, Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>>> clusterLogic = buildClusterLogicMap();

        List<AdditionalEffort> listAe = new ArrayList<>();

        List<ShiftTimeWorkDTO> allShifts = shiftTimeWorkService.getShiftTimeWorkByNameSite(SITE_NAME).stream().filter(dto -> FILTERED_SHIFT.contains(dto.getShiftCode())).toList();

        for (Entry<String, List<PerformanceRowDTO>> entry : mapAllPerformance.entrySet()) {
            ClearPerformanceEmployee clearEmployee = new ClearPerformanceEmployee();
            PerformanceRowDTO first = entry.getValue().getFirst();
            PerformanceRowDTO last = entry.getValue().getLast();

            LocalDateTime startActivityEmployee = first.getStartActivity();
            LocalDateTime endActivityEmployee = last.getEndActivity();

            ShiftTimeWork shiftTimeWork = null;

            if (!startActivityEmployee.toLocalDate().isEqual(endActivityEmployee.toLocalDate())){
                Optional<ShiftTimeWorkDTO> nightShift = allShifts.stream()
                        .filter(dto -> dto.getShiftCode().equalsIgnoreCase("III"))
                        .findFirst();

                if (nightShift.isPresent()) {
                    shiftTimeWork = entityManager.getReference(ShiftTimeWork.class, nightShift.get().getShiftId());
                    clearEmployee.setShiftId(shiftTimeWork);
                } else {
                    throw new IllegalStateException("Not fount Night Shift");
                }
            }else{
                ShiftTimeWorkDTO matchedShift = findBestMatchingShift(startActivityEmployee, endActivityEmployee, allShifts, first.getDate());
                if (matchedShift != null) {
                    shiftTimeWork = entityManager.getReference(ShiftTimeWork.class, matchedShift.getShiftId());
                    clearEmployee.setShiftId(shiftTimeWork);
                }else{
                    throw new IllegalStateException("Unknown shift for employee -> " + first.getExpertis());
                }
            }

            clearEmployee.setDate(first.getDate());
            clearEmployee.setExpertis(first.getExpertis());

            double idleTime = 0d;
            int idleCount = 0;
            Map<String, Double> mapAeForEmployee = new HashMap<>();

            for (PerformanceRowDTO row : entry.getValue()) {

                idleTime += row.getDurationIdle();
                idleCount += row.getIdleCount();

                String activityKey = row.getActivityName().toUpperCase();
                BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO> actLogic = activityLogic.get(activityKey);
                if (actLogic != null) actLogic.accept(clearEmployee, row);

                String clusterKey = row.getActivityCluster().toUpperCase();
                String finalKey = row.getFinalCluster().toUpperCase();

                if((finalKey.equalsIgnoreCase("Additional Effort") || finalKey.equalsIgnoreCase("ActivityExclude"))
                        &&
                        (clusterKey.equalsIgnoreCase("spi_II") || clusterKey.equalsIgnoreCase("spi_III"))) {

                    mapAeForEmployee.merge(
                            row.getActivityName(),
                            row.getDuration(),
                            Double::sum
                    );
                }

                Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>> clusterMap = clusterLogic.get(clusterKey);
                if (clusterMap != null) {
                    BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO> finalLogic = clusterMap.get(finalKey);
                    if (finalLogic != null) finalLogic.accept(clearEmployee, row);
                }
            }

            final ShiftTimeWork finalShift = shiftTimeWork;

            if(!mapAeForEmployee.isEmpty()){
                listAe.addAll(mapAeForEmployee.entrySet().stream().map(obj -> {
                    AdditionalEffort ae = new AdditionalEffort();
                    ae.setActivityName(activityNameMap.get(obj.getKey()));
                    ae.setDuration(obj.getValue());
                    ae.setDate(first.getDate());
                    ae.setExpertis(first.getExpertis());
                    ae.setShift(finalShift);
                    return ae;
                }).toList());
            }

            clearEmployee.setIdleCount(idleCount);
            clearEmployee.setDurationIdle(idleTime);
            clearPerformance.put(entry.getKey(), clearEmployee);
        }

        insertClearPerformanceGeneric(conn, clearPerformance.values().stream().toList(), "clear_performance_employee");
        insertAdditionalEffortBatch(conn, listAe);
    }

    private ShiftTimeWorkDTO findBestMatchingShift(LocalDateTime start, LocalDateTime end, List<ShiftTimeWorkDTO> shifts, LocalDate date) {
        return shifts.stream()
                .filter(shift -> shift.getStartTime().isBefore(shift.getEndTime()))
                .map(shift -> {
                    LocalDateTime shiftStart = date.atTime(shift.getStartTime());
                    LocalDateTime shiftEnd = date.atTime(shift.getEndTime());

                    Duration overlap = calculateOverlap(start, end, shiftStart, shiftEnd);
                    return new AbstractMap.SimpleEntry<>(shift, overlap);
                })
                .max(Comparator.comparing(entry -> entry.getValue().toMinutes()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Duration calculateOverlap(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        LocalDateTime maxStart = aStart.isAfter(bStart) ? aStart : bStart;
        LocalDateTime minEnd = aEnd.isBefore(bEnd) ? aEnd : bEnd;
        return maxStart.isBefore(minEnd) ? Duration.between(maxStart, minEnd) : Duration.ZERO;
    }

    private Map<String, BiConsumer<ClearPerformanceEmployee, Double>> buildActivityTimeOnlyLogicMap() {
        Map<String, BiConsumer<ClearPerformanceEmployee, Double>> map = new HashMap<>();

        map.put("LINESORTPACK", (e, duration) -> e.setTimeOpt(e.getTimeOpt() + duration));
        map.put("PACK_MULTI", (e, duration) -> e.setTimeMulti(e.getTimeMulti() + duration));
        map.put("PACK_SINGLE", (e, duration) -> e.setTimeSingle(e.getTimeSingle() + duration));
        map.put("SORT", (e, duration) -> e.setTimeSort(e.getTimeSort() + duration));
        map.put("WMO_OUTBOUND_REPACKING", (e, duration) -> e.setTimeWmo(e.getTimeWmo() + duration));
        map.put("NCO PACK DIRECT", (e, duration) -> e.setTimeNco(e.getTimeNco() + duration));
        map.put("PICK", (e, duration) -> e.setTimePick(e.getTimePick() + duration));
        map.put("STOW", (e, duration) -> e.setTimeStow(e.getTimeStow() + duration));
        map.put("DEFECT ITEM - CATEGORIZATION", (e, duration) -> e.setTimeReturn(e.getTimeReturn() + duration));
        map.put("FASTLANE RECEIVE", (e, duration) -> e.setTimeFast(e.getTimeFast() + duration));
        map.put("REPACKRECEIVE", (e, duration) -> e.setTimeRepack(e.getTimeRepack() + duration));
        map.put("REREPLENISHMENT", (e, duration) -> e.setTimeRepl(e.getTimeRepl() + duration));
        map.put("RECEIVE", (e, duration) -> e.setTimeCore(e.getTimeCore() + duration));

        return map;
    }

    private Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>> buildActivityLogicMap() {
        Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>> map = new HashMap<>();
        map.put("LINESORTPACK", (e, r) -> {
            e.setQlOpt(e.getQlOpt() + r.getQl());
            e.setTimeOpt(e.getTimeOpt() + r.getDuration());
        });
        map.put("PACK_MULTI", (e, r) -> {
            e.setQlMulti(e.getQlMulti() + r.getQl());
            e.setTimeMulti(e.getTimeMulti() + r.getDuration());
        });
        map.put("PACK_SINGLE", (e, r) -> {
            e.setQlSingle(e.getQlSingle() + r.getQl());
            e.setTimeSingle(e.getTimeSingle() + r.getDuration());
        });
        map.put("SORT", (e, r) -> {
            e.setQlSort(e.getQlSort() + r.getQl());
            e.setTimeSort(e.getTimeSort() + r.getDuration());
        });
        map.put("WMO_OUTBOUND_REPACKING", (e, r) -> {
            e.setQlWmo(e.getQlWmo() + r.getQl());
            e.setTimeWmo(e.getTimeWmo() + r.getDuration());
        });
        map.put("NCO PACK DIRECT", (e, r) -> {
            e.setQlNco(e.getQlNco() + r.getQl());
            e.setTimeNco(e.getTimeNco() + r.getDuration());
        });
        map.put("PICK", (e, r) -> {
            e.setQlPick(e.getQlPick() + r.getQl());
            e.setTimePick(e.getTimePick() + r.getDuration());
            e.setPickNos1(e.getPickNos1() + r.getPickNos1());
            e.setPickNos2(e.getPickNos2() + r.getPickNos2());
        });
        map.put("STOW", (e, r) -> {
            e.setQlStow(e.getQlStow() + r.getQl());
            e.setTimeStow(e.getTimeStow() + r.getDuration());
            e.setStowClarifications(e.getStowClarifications() + r.getStowClarifications());
        });
        map.put("DEFECT ITEM - CATEGORIZATION", (e, r) -> {
            e.setQlReturn(e.getQlReturn() + r.getQl());
            e.setTimeReturn(e.getTimeReturn() + r.getDuration());
        });
        map.put("DEFECT ITEM - DEFECT SORT", (e, r) -> {
            e.setQlReturnSort(e.getQlReturnSort() + r.getQl());
            e.setTimeReturnSort(e.getTimeReturnSort() + r.getDuration());
        });
        map.put("VOLUMESCAN", (e, r) -> {
            e.setQlVolumeScan(e.getQlVolumeScan() + r.getQl());
            e.setTimeVolumeScan(e.getTimeVolumeScan() + r.getDuration());
        });
        map.put("STOCKTAKING direct", (e, r) -> {
            e.setQlStocktaking(e.getQlStocktaking() + r.getQl());
            e.setTimeStocktaking(e.getTimeStocktaking() + r.getDuration());
        });
        map.put("RELOCATION direct", (e, r) -> {
            e.setQlRelocation(e.getQlRelocation() + r.getQl());
            e.setTimeRelocation(e.getTimeRelocation() + r.getDuration());
        });
        map.put("FASTLANE RECEIVE", (e, r) -> {
            e.setQlFast(e.getQlFast() + r.getQl());
            e.setTimeFast(e.getTimeFast() + r.getDuration());
        });
        map.put("REPACKRECEIVE", (e, r) -> {
            e.setQlRepack(e.getQlRepack() + r.getQl());
            e.setTimeRepack(e.getTimeRepack() + r.getDuration());
        });
        map.put("REREPLENISHMENT", (e, r) -> {
            e.setQlRepl(e.getQlRepl() + r.getQl());
            e.setTimeRepl(e.getTimeRepl() + r.getDuration());
        });
        map.put("RECEIVE", (e, r) -> {
            e.setQlCore(e.getQlCore() + r.getQl());
            e.setQlCoreBoxed(e.getQlCoreBoxed() + r.getQlBox());
            e.setQlCoreHanging(e.getQlCoreHanging() + r.getQlHanging());
            e.setQlCoreShoes(e.getQlCoreShoes() + r.getQlShoes());
            e.setQlCoreBoots(e.getQlCoreBoots() + r.getQlBoots());
            e.setQlCoreOther(e.getQlCoreOther() + r.getQlOther());
            e.setTimeCore(e.getTimeCore() + r.getDuration());
        });
        return map;
    }

    private Map<String, Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>>> buildClusterLogicMap() {
        Map<String, Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>>> clusterMap = new HashMap<>();

        Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>> spiII = new HashMap<>();
        spiII.put("LINESORTER PACK", (e, r) -> e.setNttOpt(e.getNttOpt() + r.getDuration()));
        spiII.put("STANDARD PACK MULTI", (e, r) -> e.setNttMulti(e.getNttMulti() + r.getDuration()));
        spiII.put("STANDARD PACK SINGLE", (e, r) -> e.setNttSingle(e.getNttSingle() + r.getDuration()));
        spiII.put("PACK", (e, r) -> e.setNttPack(e.getNttPack() + r.getDuration()));
        spiII.put("MANUAL SORT", (e, r) -> e.setNttSort(e.getNttSort() + r.getDuration()));
        spiII.put("INTERNAL ORDERS SHIPPING", (e, r) -> e.setNttWmo(e.getNttWmo() + r.getDuration()));
        spiII.put("NCO SHIPPING", (e, r) -> e.setNttNco(e.getNttNco() + r.getDuration()));
        spiII.put("CORE STOW", (e, r) -> e.setNttStow(e.getNttStow() + r.getDuration()));
        spiII.put("PICK", (e, r) -> e.setNttPick(e.getNttPick() + r.getDuration()));
        spiII.put("SHIPPING", (e, r) -> e.setNttShipping(e.getNttShipping() + r.getDuration()));
        spiII.put("CORE RETOURE", (e, r) -> e.setNttReturn(e.getNttReturn() + r.getDuration()));
        spiII.put("REFURBISHMENT", (e, r) -> e.setNttReturn(e.getNttReturn() + r.getDuration()));
        spiII.put("FASTLANE RECEIVE", (e, r) -> e.setNttFast(e.getNttFast() + r.getDuration()));
        spiII.put("CORE RECEIVE", (e, r) -> e.setNttCore(e.getNttCore() + r.getDuration()));
        spiII.put("GOODSRECEIVE", (e, r) -> e.setNttGoods(e.getNttGoods() + r.getDuration()));

        Map<String, BiConsumer<ClearPerformanceEmployee, PerformanceRowDTO>> spiIII = new HashMap<>();
        spiIII.put("LINESORTER PACK", (e, r) -> e.setSupportOpt(e.getSupportOpt() + r.getDuration()));
        spiIII.put("PACK", (e, r) -> e.setSupportPack(e.getSupportPack() + r.getDuration()));
        spiIII.put("MANUAL SORT", (e, r) -> e.setSupportSort(e.getSupportSort() + r.getDuration()));
        spiIII.put("INTERNAL ORDERS SHIPPING", (e, r) -> e.setSupportWmo(e.getSupportWmo() + r.getDuration()));
        spiIII.put("NCO SHIPPING", (e, r) -> e.setSupportNco(e.getSupportNco() + r.getDuration()));
        spiIII.put("CORE STOW", (e, r) -> e.setSupportStow(e.getSupportStow() + r.getDuration()));
        spiIII.put("PICK", (e, r) -> e.setSupportPick(e.getSupportPick() + r.getDuration()));
        spiIII.put("SHIPPING", (e, r) -> e.setSupportShipping(e.getSupportShipping() + r.getDuration()));
        spiIII.put("REFURBISHMENT", (e, r) -> e.setSupportReturn(e.getSupportReturn() + r.getDuration()));
        spiIII.put("REPACKRECEIVE", (e, r) -> e.setSupportRepack(e.getSupportRepack() + r.getDuration()));
        spiIII.put("CORE RECEIVE", (e, r) -> e.setSupportCore(e.getSupportCore() + r.getDuration()));
        spiIII.put("GOODSRECEIVE", (e, r) -> e.setSupportGoods(e.getSupportGoods() + r.getDuration()));

        clusterMap.put("SPI_II", spiII);
        clusterMap.put("SPI_III", spiIII);
        return clusterMap;
    }

    private void copyInsertPerformance(Connection conn, List<PerformanceRow> list) {
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
            sb.append(p.getDurationIdle()).append(';');
            sb.append(p.getIdleCount()).append(';');
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
                        COPY performance_gd.performance (
                           date, expertis, activity_name_id, final_cluster_id, activity_cluster_id,
                           start_activity, end_activity, duration, duration_idle, idle_count,
                           ql, ql_box, ql_hanging, ql_shoes, ql_boots, ql_other,
                           stow_clarifications, pick_nos1, pick_nos2
                        )
                        FROM STDIN
                        DELIMITER ';'
                        CSV
                    """;

            log.info("Starting copyIn (expected columns: 19 per line) ... : {}", LocalDateTime.now());

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


    private String formatTimestamp(LocalDateTime inst) {
        if (inst == null) {
            return "";
        }
        return TIMESTAMP_FORMAT.format(inst);
    }

    private LocalDate parseDateToLocalDate(String date) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, DATETIME_FORMAT);
        return zonedDateTime.toLocalDate();
    }

    private LocalDateTime parseDateTime(String raw) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(raw, DATETIME_FORMAT);
        return zonedDateTime.toLocalDateTime();
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

        log.info("Check partition {} for range {} to {}", partitionName, partitionStart, partitionEnd);

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM information_schema.tables WHERE table_schema = 'performance_gd' AND table_name = ?")) {
            ps.setString(1, partitionName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    log.info("Partition {} not exist. Create...", partitionName);
                    try (Statement stmt = conn.createStatement()) {
                        String createPartitionSql = String.format(
                                "CREATE TABLE performance_gd.%s PARTITION OF performance_gd.performance " +
                                        "FOR VALUES FROM ('%s'::date) TO ('%s'::date)",
                                partitionName, partitionStart, partitionEnd
                        );
                        stmt.execute(createPartitionSql);
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_date ON performance_gd.%s (date)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_expertis ON performance_gd.%s (expertis)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_activity_name_id ON performance_gd.%s (activity_name_id)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_final_cluster_id ON performance_gd.%s (final_cluster_id)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_start_activity ON performance_gd.%s (start_activity)", partitionName, partitionName));
                        stmt.execute(String.format("CREATE INDEX IF NOT EXISTS idx_%s_end_activity ON performance_gd.%s (end_activity)", partitionName, partitionName));
                    }
                    log.info("Partition {} done.", partitionName);
                } else {
                    log.info("Partition {} is exist.", partitionName);
                }
            }
        } catch (SQLException e) {
            log.error("Error in processing creating partition", e);
            throw new RuntimeException(e);
        }
    }

    public <T extends ClearPerformanceData> void insertClearPerformanceGeneric(Connection conn, List<T> list, String tableName) throws SQLException {
        String sql = "INSERT INTO performance_gd." + tableName + " (" +
                "date, expertis, shift_id, " +
                "time_opt, time_multi, time_single, time_sort, time_wmo, time_nco, " +
                "time_pick, time_stow, time_return, time_fast, time_repack, time_repl, time_core, " +
                "ntt_opt, ntt_multi, ntt_single, ntt_pack, ntt_sort, ntt_wmo, ntt_nco, " +
                "ntt_stow, ntt_pick, ntt_shipping, ntt_return, ntt_fast, ntt_core, ntt_goods, " +
                "support_opt, support_pack, support_sort, support_wmo, support_nco, " +
                "support_stow, support_pick, support_shipping, support_return, support_repack, support_core, support_goods," +
                "duration_idle, idle_count, ql_relocation, ql_volume_scan, ql_return_sort, ql_stocktaking, " +
                "ql_opt, ql_multi, ql_single, ql_sort, ql_wmo, ql_nco, " +
                "ql_stow, ql_pick, ql_return, ql_fast, ql_repack, ql_repl, ql_core, " +
                "ql_core_boxed, ql_core_hanging, ql_core_shoes, ql_core_boots, ql_core_other, " +
                "time_relocation, time_volume_scan, time_return_sort, time_stocktaking, " +
                "pick_nos_1, pick_nos_2, stow_clarifications" +
                ") VALUES (" + "?,".repeat(73).replaceAll(",$", "") + ")";

        int count = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (T emp : list) {
                int i = 1;
                ps.setDate(i++, java.sql.Date.valueOf(emp.getDate()));
                ps.setString(i++, emp.getExpertis());
                ps.setLong(i++, emp.getShiftId().getId());

                ps.setDouble(i++, emp.getTimeOpt());
                ps.setDouble(i++, emp.getTimeMulti());
                ps.setDouble(i++, emp.getTimeSingle());
                ps.setDouble(i++, emp.getTimeSort());
                ps.setDouble(i++, emp.getTimeWmo());
                ps.setDouble(i++, emp.getTimeNco());
                ps.setDouble(i++, emp.getTimePick());
                ps.setDouble(i++, emp.getTimeStow());
                ps.setDouble(i++, emp.getTimeReturn());
                ps.setDouble(i++, emp.getTimeFast());
                ps.setDouble(i++, emp.getTimeRepack());
                ps.setDouble(i++, emp.getTimeRepl());
                ps.setDouble(i++, emp.getTimeCore());

                ps.setDouble(i++, emp.getNttOpt());
                ps.setDouble(i++, emp.getNttMulti());
                ps.setDouble(i++, emp.getNttSingle());
                ps.setDouble(i++, emp.getNttPack());
                ps.setDouble(i++, emp.getNttSort());
                ps.setDouble(i++, emp.getNttWmo());
                ps.setDouble(i++, emp.getNttNco());
                ps.setDouble(i++, emp.getNttStow());
                ps.setDouble(i++, emp.getNttPick());
                ps.setDouble(i++, emp.getNttShipping());
                ps.setDouble(i++, emp.getNttReturn());
                ps.setDouble(i++, emp.getNttFast());
                ps.setDouble(i++, emp.getNttCore());
                ps.setDouble(i++, emp.getNttGoods());

                ps.setDouble(i++, emp.getSupportOpt());
                ps.setDouble(i++, emp.getSupportPack());
                ps.setDouble(i++, emp.getSupportSort());
                ps.setDouble(i++, emp.getSupportWmo());
                ps.setDouble(i++, emp.getSupportNco());
                ps.setDouble(i++, emp.getSupportStow());
                ps.setDouble(i++, emp.getSupportPick());
                ps.setDouble(i++, emp.getSupportShipping());
                ps.setDouble(i++, emp.getSupportReturn());
                ps.setDouble(i++, emp.getSupportRepack());
                ps.setDouble(i++, emp.getSupportCore());
                ps.setDouble(i++, emp.getSupportGoods());

                ps.setDouble(i++, emp.getDurationIdle());
                ps.setDouble(i++, emp.getIdleCount());
                ps.setDouble(i++, emp.getQlRelocation());
                ps.setDouble(i++, emp.getQlVolumeScan());
                ps.setDouble(i++, emp.getQlReturnSort());
                ps.setDouble(i++, emp.getQlStocktaking());

                ps.setInt(i++, emp.getQlOpt());
                ps.setInt(i++, emp.getQlMulti());
                ps.setInt(i++, emp.getQlSingle());
                ps.setInt(i++, emp.getQlSort());
                ps.setInt(i++, emp.getQlWmo());
                ps.setInt(i++, emp.getQlNco());
                ps.setInt(i++, emp.getQlStow());
                ps.setInt(i++, emp.getQlPick());
                ps.setInt(i++, emp.getQlReturn());
                ps.setInt(i++, emp.getQlFast());
                ps.setInt(i++, emp.getQlRepack());
                ps.setInt(i++, emp.getQlRepl());
                ps.setInt(i++, emp.getQlCore());
                ps.setInt(i++, emp.getQlCoreBoxed());
                ps.setInt(i++, emp.getQlCoreHanging());
                ps.setInt(i++, emp.getQlCoreShoes());
                ps.setInt(i++, emp.getQlCoreBoots());
                ps.setInt(i++, emp.getQlCoreOther());

                ps.setDouble(i++, emp.getTimeRelocation());
                ps.setDouble(i++, emp.getTimeVolumeScan());
                ps.setDouble(i++, emp.getTimeReturnSort());
                ps.setDouble(i++, emp.getTimeStocktaking());

                ps.setInt(i++, emp.getPickNos1());
                ps.setInt(i++, emp.getPickNos2());
                ps.setInt(i++, emp.getStowClarifications());

                ps.addBatch();
                count++;

                if (count % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            if (count % BATCH_SIZE != 0) {
                ps.executeBatch();
                ps.clearBatch();
            }
        }
    }

    public void insertAdditionalEffortBatch(Connection conn, List<AdditionalEffort> efforts) throws SQLException {
        String sql = "INSERT INTO performance_gd.additional_effort (" +
                "date, expertis, activity_name_id, duration, shift_id" +
                ") VALUES (?, ?, ?, ?, ?)";

        int count = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (AdditionalEffort effort : efforts) {
                int i = 1;
                ps.setDate(i++, java.sql.Date.valueOf(effort.getDate()));
                ps.setString(i++, effort.getExpertis());
                ps.setLong(i++, effort.getActivityName().getId());
                ps.setDouble(i++, effort.getDuration());
                ps.setLong(i++, effort.getShift().getId());

                ps.addBatch();
                count++;

                if (count % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }

            if (count % BATCH_SIZE != 0) {
                ps.executeBatch();
                ps.clearBatch();
            }
        }
    }
}
