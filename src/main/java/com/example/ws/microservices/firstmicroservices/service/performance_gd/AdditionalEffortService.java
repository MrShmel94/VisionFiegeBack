package com.example.ws.microservices.firstmicroservices.service.performance_gd;

import com.example.ws.microservices.firstmicroservices.entity.performance_gd.AdditionalEffort;

import java.util.List;

public interface AdditionalEffortService {
    void saveAdditionalEffort(List<AdditionalEffort> additionalEfforts);
}
