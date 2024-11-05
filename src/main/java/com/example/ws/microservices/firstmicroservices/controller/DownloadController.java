package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.configuration.FileUploadConfig;
import com.example.ws.microservices.firstmicroservices.customError.MissingHeadersException;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.CheckHeaderService;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.PerformanceService;
import com.example.ws.microservices.firstmicroservices.utils.XlsxParserGDUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/download")
@RequiredArgsConstructor
public class DownloadController {

    private final CheckHeaderService checkHeaderService;
    private final PerformanceService performanceService;
    private final FileUploadConfig fileUploadConfig;


    @PostMapping("/uploadPerformanceGD")
    public ResponseEntity<List<String>>  uploadFile(@RequestParam("file") List<MultipartFile> files) {


        List<String> result = new ArrayList<>();

        if (files.size() > fileUploadConfig.getMaxFileCount()) {
            log.info("");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of("Too many files uploaded. Maximum allowed: " + fileUploadConfig.getMaxFileCount()));
        }

        try {
            Map<String, String> checkHeaders = checkHeaderService.getAllCheckHeaders();
            List<String> onlyTableNames = checkHeaders.values().stream().toList();

            files.forEach(eachFile -> {
                try {

                    if (eachFile.getSize() > fileUploadConfig.getMaxFileSize()) {
                        result.add(eachFile.getOriginalFilename() + " - Error: File size exceeds the limit of " + (fileUploadConfig.getMaxFileSize() / (1024 * 1024)) + "MB.");
                        return;
                    }

                    List<List<String>> parsedFile = XlsxParserGDUtils.parserXlsxFile(eachFile.getInputStream(), onlyTableNames, 2, eachFile.getName());
                    performanceService.processFile(parsedFile, checkHeaders, onlyTableNames);

                    result.add(eachFile.getOriginalFilename() + " - Successfully processed. Total rows: " + parsedFile.size());

                } catch (MissingHeadersException e) {
                    result.add(eachFile.getOriginalFilename() + " - Error: Missing required headers.");
                } catch (IOException e) {
                    result.add(eachFile.getOriginalFilename() + " - Error: Unable to read the file.");
                } catch (Exception e) {
                    result.add(eachFile.getOriginalFilename() + " - Error: Unexpected error occurred during processing.");
                }
            });

            return ResponseEntity.ok().body(result);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("An error occurred while processing the request. Please try again later."));
        }
    }
}
