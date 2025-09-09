package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.performance.gd.CheckNameFileDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd.CheckNameFileRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.performance_gd.CheckNameFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckNameFileServiceImpl implements CheckNameFileService {

    private final CheckNameFileRepository checkNameFileRepository;

    @Override
    public List<CheckNameFileDTO> getFilesName(){
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        return checkNameFileRepository.findAllByDateBetween(start, end);
    }
}
