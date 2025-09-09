package com.example.ws.microservices.firstmicroservices.serviceImpl.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.dto.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.Department;
import com.example.ws.microservices.firstmicroservices.repository.cache.DepartmentRepositoryCache;
import com.example.ws.microservices.firstmicroservices.service.cache.DepartmentServiceCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DepartmentServiceCacheImpl implements DepartmentServiceCache {

    private final DepartmentRepositoryCache departmentRepositoryCache;

    @Override
    public List<Department> findAll() {
        return departmentRepositoryCache.findAll();
    }

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<DepartmentDTO> departments = getAllFromDB();
        redisTemplate.opsForValue().set("departments", departments);
    }

    @Override
    public List<DepartmentDTO> getAllFromDB() {
        return departmentRepositoryCache.findAllWithSite().stream().map(eachObject -> DepartmentDTO.builder()
                .name(eachObject.getName())
                .id(eachObject.getId())
                .siteName(eachObject.getSite().getName())
                .build()).toList();
    }

    @Override
    public boolean supportsType(Class<?> type) {
        return type.equals(DepartmentDTO.class);
    }


}
