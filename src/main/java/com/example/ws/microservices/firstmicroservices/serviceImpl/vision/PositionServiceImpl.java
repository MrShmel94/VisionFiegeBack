package com.example.ws.microservices.firstmicroservices.serviceImpl.vision;

import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.PositionDTO;
import com.example.ws.microservices.firstmicroservices.repository.vision.PositionRepository;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.secure.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.UserService;
import com.example.ws.microservices.firstmicroservices.service.cache.PositionServiceCache;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.service.vision.PositionService;
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
    private final RedisCacheService redisCacheService;

    @Override
    public List<PositionDTO> getPositions() {
        return redisCacheService.getFromCache("positions", new TypeReference<List<PositionDTO>>() {}).orElseGet(() -> {
           List<PositionDTO> allDto = positionServiceCache.getAllFromDB();
           redisCacheService.saveToCache("positions", allDto);
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
