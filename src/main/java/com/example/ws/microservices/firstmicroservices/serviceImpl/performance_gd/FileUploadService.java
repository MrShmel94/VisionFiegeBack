package com.example.ws.microservices.firstmicroservices.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.service.performance_gd.CheckHeaderService;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.PerformanceService;
import com.example.ws.microservices.firstmicroservices.utils.XlsxParserGDUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final DataSource dataSource;
    private final CheckNameFileServiceCustom checkNameFileServiceCustom;
    private final CheckHeaderService checkHeaderService;
    private final PerformanceService performanceService;

    /**
     * Processes the uploaded file by executing a manual transaction.
     * It reserves a record in the check_name_file table with status IN_PROGRESS,
     * parses the Excel file, performs a COPY insertion, updates the status to DONE,
     * and commits the transaction. If any error occurs, the transaction is rolled back.
     *
     * @param file the uploaded MultipartFile
     */
    public void handleUpload(MultipartFile file) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";

        if(!checkOriginalNameFile(fileName)) {
            log.error("Error while processing file, incorrect name file: {}", fileName);
            throw new RuntimeException("Upload failed for file " + fileName);
        }

        String normalFileName = normalizeFileName(fileName);

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            long recordId = checkNameFileServiceCustom.insertInProgress(conn, LocalDate.now(), userId, normalFileName);
            try {

                Map<String, String> headerMap = checkHeaderService.getAllCheckHeaders();
                List<String> onlyTableNames = List.copyOf(headerMap.values());

                List<List<String>> parsedData = XlsxParserGDUtils.parserXlsxFile(file.getInputStream(), onlyTableNames, 2, normalFileName);
                performanceService.processFile(conn, parsedData, headerMap, onlyTableNames);
                checkNameFileServiceCustom.updateStatus(conn, recordId, "DONE");
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception ex) {
            log.error("Error while processing file: {}: {}" , fileName, ex.getMessage());
            throw new RuntimeException("Upload failed for file " + fileName + ": " + ex.getMessage());
        }
    }

    public boolean checkOriginalNameFile(String fileName){
        String pattern = "^WH Productivity Daily \\d{4}-\\d{2}-\\d{2}(\\s.*)?$";
        return fileName.matches(pattern);
    }

    public static String normalizeFileName(String fileName) {
        Pattern pattern = Pattern.compile("^(WH Productivity Daily \\d{4}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        log.error("Name file is incorrect format: {}", fileName);
        throw new IllegalArgumentException("Name file is incorrect format: " + fileName);
    }

}
