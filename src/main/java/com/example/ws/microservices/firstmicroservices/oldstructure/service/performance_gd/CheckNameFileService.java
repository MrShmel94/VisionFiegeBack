package com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd.CheckNameFileDTO;

import java.util.List;

public interface CheckNameFileService {
    List<CheckNameFileDTO> getFilesName();
}
