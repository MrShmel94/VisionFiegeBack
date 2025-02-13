package com.example.ws.microservices.firstmicroservices.serviceImpl.templateTables;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.entity.template.Department;
import com.example.ws.microservices.firstmicroservices.repository.templateTables.DepartmentRepository;
import com.example.ws.microservices.firstmicroservices.service.templateTables.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<DepartmentDTO> departments = getAllFromDB();
        for (DepartmentDTO department : departments) {
            redisTemplate.opsForValue().set("department:" + department.getId(), department);
        }
    }

    @Override
    public List<DepartmentDTO> getAllFromDB() {
        return departmentRepository.findAllWithSite().stream().map(eachObject -> DepartmentDTO.builder()
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
