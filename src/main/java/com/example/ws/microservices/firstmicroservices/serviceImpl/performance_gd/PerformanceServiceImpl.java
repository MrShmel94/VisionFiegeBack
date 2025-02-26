package com.example.ws.microservices.firstmicroservices.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.dto.PerformanceDTO;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.*;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.ActivityNameService;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.FinalClusterService;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.PerformanceService;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.SpiClusterService;
import com.example.ws.microservices.firstmicroservices.serviceImpl.ClickHousePerformanceServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private ActivityNameService activityNameService;
    private FinalClusterService finalClusterService;
    private SpiClusterService spiClusterService;
    private final JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                            .map(checkHeaderList::get)
                            .map(indexMap::get)
                            .map(eachIndex -> {
                                try {
                                    return (int) Double.parseDouble(eachProcessLine.get(eachIndex));
                                } catch (NumberFormatException e) {
                                    return 0;
                                }
                            })
                            .reduce(0, Integer::sum).shortValue()
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
            sb.append(p.getDuration() == null ? "0" : p.getDuration().toString()).append(';');
            sb.append(nullSafeShort(p.getQl())).append(';');
            sb.append(nullSafeShort(p.getQlBox())).append(';');
            sb.append(nullSafeShort(p.getQlHanging())).append(';');
            sb.append(nullSafeShort(p.getQlShoes())).append(';');
            sb.append(nullSafeShort(p.getQlBoots())).append(';');
            sb.append(nullSafeShort(p.getQlOther())).append(';');
            sb.append(nullSafeShort(p.getStowClarifications())).append(';');
            sb.append(nullSafeShort(p.getPickNos1())).append(';');
            sb.append(nullSafeShort(p.getPickNos2())).append('\n');
        }

        log.info("Finished building CSV. Total lines: {}", list.size());

        log.info("End processing StringBuilder : {}", LocalDateTime.now());

        byte[] csvData = sb.toString().getBytes();

        log.info("CSV data size in bytes: {}", csvData.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(csvData);

        try {
            var pgConn = conn.unwrap(org.postgresql.PGConnection.class);

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

    private short nullSafeShort(Short val) {
        return (val == null) ? 0 : val;
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

    private Short safeParseShort(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0;
        }

        try {
            double value = Double.parseDouble(str);
            if (value == 0.0) {
                return 0;
            }
            return (short) value;
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
