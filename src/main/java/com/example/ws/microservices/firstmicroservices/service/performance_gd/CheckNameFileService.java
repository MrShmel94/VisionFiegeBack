package com.example.ws.microservices.firstmicroservices.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.dto.performance.gd.CheckNameFileDTO;

import java.util.List;

public interface CheckNameFileService {
    List<CheckNameFileDTO> getFilesName();
}
