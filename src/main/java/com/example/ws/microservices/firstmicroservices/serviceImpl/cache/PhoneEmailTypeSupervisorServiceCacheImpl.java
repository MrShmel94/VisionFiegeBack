package com.example.ws.microservices.firstmicroservices.serviceImpl.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.dto.PhoneEmailTypeSupervisorDTO;
import com.example.ws.microservices.firstmicroservices.repository.cache.PhoneEmailTypeSupervisorRepositoryCache;
import com.example.ws.microservices.firstmicroservices.service.cache.PhoneEmailTypeSupervisorServiceCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PhoneEmailTypeSupervisorServiceCacheImpl implements PhoneEmailTypeSupervisorServiceCache {

    private final PhoneEmailTypeSupervisorRepositoryCache phoneEmailTypeSupervisorRepositoryCache;

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<PhoneEmailTypeSupervisorDTO> allTypes = phoneEmailTypeSupervisorRepositoryCache.getAllType();

        redisTemplate.opsForValue().set("typePhoneAndEmail", allTypes);

//        for (PhoneEmailTypeSupervisorDTO type : allTypes) {
//            redisTemplate.opsForValue().set("typePhoneAndEmail:" + type.getId(), type);
//        }
    }

    @Override
    public List<PhoneEmailTypeSupervisorDTO> getAllFromDB() {
        return phoneEmailTypeSupervisorRepositoryCache.getAllType();
    }

    @Override
    public boolean supportsType(Class<?> type) {
        return type.equals(PhoneEmailTypeSupervisorDTO.class);
    }
}
