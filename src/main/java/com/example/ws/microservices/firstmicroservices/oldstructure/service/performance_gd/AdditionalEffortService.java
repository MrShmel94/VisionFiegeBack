package com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.AdditionalEffort;

import java.util.List;

public interface AdditionalEffortService {
    void saveAdditionalEffort(List<AdditionalEffort> additionalEfforts);
}
