package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.position;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.supervisorassignment.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.common.security.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.common.security.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service.UserService;
import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final UserService userService;
    private final PositionServiceCache positionServiceCache;
    private final RedisService redisService;

    @Override
    public List<PositionDTO> getPositions() {
        return redisService.getFromCache("positions", new TypeReference<List<PositionDTO>>() {}).orElseGet(() -> {
           List<PositionDTO> allDto = positionServiceCache.getAllFromDB();
           redisService.saveToCache("positions", allDto);
           return allDto;
        });
    }

    @Override
    public List<PositionDTO> getPositionsBySupervisorSite(String userId) {
        SupervisorAllInformationDTO usersDTO = userService.getSupervisorAllInformation(null, userId);

        return getPositions().stream().filter(pos -> pos.getSiteName().equalsIgnoreCase(usersDTO.getSiteName())).toList();
    }

    @Override
    public List<PositionDTO> getPositionsBySupervisorSite() {
        CustomUserDetails users = new SecurityUtils().getCurrentUser();
        SupervisorAllInformationDTO usersDTO = userService.getSupervisorAllInformation(null, users.getUserId());

        return getPositions().stream().filter(pos -> pos.getSiteName().equalsIgnoreCase(usersDTO.getSiteName())).toList();
    }
}
