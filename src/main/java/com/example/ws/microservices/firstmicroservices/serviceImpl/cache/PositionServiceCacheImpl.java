package com.example.ws.microservices.firstmicroservices.serviceImpl.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.dto.PositionDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.Position;
import com.example.ws.microservices.firstmicroservices.repository.cache.PositionRepositoryCache;
import com.example.ws.microservices.firstmicroservices.service.cache.PositionServiceCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PositionServiceCacheImpl implements PositionServiceCache {

    private final PositionRepositoryCache positionRepositoryCache;

    @Override
    public List<Position> getAllPositionDTO() {
        return List.of();
    }

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<PositionDTO> positions = getAllFromDB();
        redisTemplate.opsForValue().set("positions", positions);
    }

    @Override
    public List<PositionDTO> getAllFromDB() {
        return positionRepositoryCache.findAllWithSite().stream().map(eachObject -> PositionDTO.builder()
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
