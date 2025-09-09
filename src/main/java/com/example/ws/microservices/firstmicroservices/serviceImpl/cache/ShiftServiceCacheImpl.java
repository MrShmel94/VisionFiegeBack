package com.example.ws.microservices.firstmicroservices.serviceImpl.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.dto.ShiftDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Shift;
import com.example.ws.microservices.firstmicroservices.repository.cache.ShiftRepositoryCache;
import com.example.ws.microservices.firstmicroservices.service.cache.ShiftServiceCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ShiftServiceCacheImpl implements ShiftServiceCache {

    private final ShiftRepositoryCache shiftRepositoryCache;

    @Override
    public List<Shift> findAll() {
        return shiftRepositoryCache.findAll();
    }

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<ShiftDTO> shifts = getAllFromDB();
        redisTemplate.opsForValue().set("shifts", shifts);
    }

    @Override
    public List<ShiftDTO> getAllFromDB() {
        return shiftRepositoryCache.findAllWithSite().stream().map(eachObject -> ShiftDTO.builder()
                .name(eachObject.getName())
                .id(eachObject.getId())
                .siteName(eachObject.getSite().getName())
                .build()).toList();
    }

    @Override
    public boolean supportsType(Class<?> type) {
        return type.equals(ShiftDTO.class);
    }
}
