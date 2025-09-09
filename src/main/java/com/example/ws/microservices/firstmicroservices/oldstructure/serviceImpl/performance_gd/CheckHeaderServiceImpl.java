package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.entity.performance_gd.CheckHeader;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd.CheckHeaderRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd.CheckHeaderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CheckHeaderServiceImpl implements CheckHeaderService {

    private final CheckHeaderRepository checkHeaderRepository;

    @Override
    public Map<String, String> getAllCheckHeaders() {
        return checkHeaderRepository.findAll().stream().collect(Collectors.toMap(
                CheckHeader::getName,
                CheckHeader::getTableName
        ));
    }
}
