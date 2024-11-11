package com.example.ws.microservices.firstmicroservices.serviceImpl.templateTables;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.PositionDTO;
import com.example.ws.microservices.firstmicroservices.entity.template.Department;
import com.example.ws.microservices.firstmicroservices.entity.template.Position;
import com.example.ws.microservices.firstmicroservices.repository.templateTables.DepartmentRepository;
import com.example.ws.microservices.firstmicroservices.repository.templateTables.PositionRepository;
import com.example.ws.microservices.firstmicroservices.service.templateTables.DepartmentService;
import com.example.ws.microservices.firstmicroservices.service.templateTables.PositionService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;

    @Override
    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<PositionDTO> positions = findAllWithSite();
        for (PositionDTO position : positions) {
            redisTemplate.opsForValue().set("position:" + position.getId(), position);
        }
    }

    @Override
    public List<PositionDTO> findAllWithSite() {
        return positionRepository.findAllWithSite().stream().map(eachObject -> PositionDTO.builder()
                .name(eachObject.getName())
                .id(eachObject.getId())
                .siteName(eachObject.getSite().getName())
                .build()).toList();
    }

    @Override
    public boolean supportsType(Class<?> type) {
        return type.equals(PositionDTO.class);
    }
}
