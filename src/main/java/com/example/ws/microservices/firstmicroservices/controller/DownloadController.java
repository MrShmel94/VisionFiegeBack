package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.customError.MissingHeadersException;
import com.example.ws.microservices.firstmicroservices.entity.performance_gd.CheckHeader;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.CheckHeaderService;
import com.example.ws.microservices.firstmicroservices.service.performance_gd.PerformanceService;
import com.example.ws.microservices.firstmicroservices.serviceImpl.performance_gd.CheckHeaderServiceImpl;
import com.example.ws.microservices.firstmicroservices.utils.XlsxParserGDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/download")
@RequiredArgsConstructor
public class DownloadController {

    private final CheckHeaderService checkHeaderService;
    private final PerformanceService performanceService;

    @PostMapping("/uploadPerformanceGD")
    public List<List<String>> uploadFile(@RequestParam("file") List<MultipartFile> files) {
        List<List<String>> result = new ArrayList<>();

        try {

            Map<String, String> checkHeaders = checkHeaderService.getAllCheckHeaders();
            List<String> onlyTableNames = checkHeaders.keySet().stream().toList();

            files.forEach(eachFile -> {
                try {
                    List<List<String>> parsedFile = XlsxParserGDUtils.parserXlsxFile(eachFile.getInputStream(), onlyTableNames, 2, eachFile.getName());
                    performanceService.processFile(parsedFile, checkHeaders, onlyTableNames);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (MissingHeadersException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
