package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.AdditionalEffort;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd.AdditionalEffortRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd.AdditionalEffortService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdditionalEffortServiceImpl implements AdditionalEffortService {

    private final AdditionalEffortRepository additionalEffortRepository;

    @Override
    public void saveAdditionalEffort(List<AdditionalEffort> additionalEfforts) {
        additionalEffortRepository.saveAll(additionalEfforts);
    }
}
