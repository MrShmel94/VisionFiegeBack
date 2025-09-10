package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl;

import com.example.ws.microservices.firstmicroservices.oldstructure.dto.attendance.gd.ShiftTimeWorkDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.ShiftTimeWorkRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.ShiftTimeWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShiftTimeWorkServiceImpl implements ShiftTimeWorkService {

    private final ShiftTimeWorkRepository shiftTimeWorkRepository;

    @Override
    public List<ShiftTimeWorkDTO> getShiftTimeWorkByNameSite(String siteName) {
        return shiftTimeWorkRepository.findShiftTimeWorkBySiteName(siteName).stream()
                .map(obj -> {
                    return ShiftTimeWorkDTO.builder()
                            .shiftId(obj.getId())
                            .shiftName(obj.getName())
                            .startTime(obj.getStartTime())
                            .endTime(obj.getEndTime())
                            .shiftCode(obj.getShiftCode())
                            .build();
                })
                .toList();
    }
}
