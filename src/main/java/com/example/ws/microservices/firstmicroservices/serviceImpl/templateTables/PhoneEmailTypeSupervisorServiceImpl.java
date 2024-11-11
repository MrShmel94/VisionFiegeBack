package com.example.ws.microservices.firstmicroservices.serviceImpl.templateTables;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.PhoneEmailTypeSupervisorDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.PositionDTO;
import com.example.ws.microservices.firstmicroservices.repository.templateTables.PhoneEmailTypeSupervisorRepository;
import com.example.ws.microservices.firstmicroservices.service.templateTables.PhoneEmailTypeSupervisorService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PhoneEmailTypeSupervisorServiceImpl implements PhoneEmailTypeSupervisorService {

    private final PhoneEmailTypeSupervisorRepository phoneEmailTypeSupervisorRepository;

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<PhoneEmailTypeSupervisorDTO> allTypes = phoneEmailTypeSupervisorRepository.getAllType();

        for (PhoneEmailTypeSupervisorDTO type : allTypes) {
            redisTemplate.opsForValue().set("typePhoneAndEmail:" + type.getId(), type);
        }
    }

    @Override
    public List<PhoneEmailTypeSupervisorDTO> findAllWithSite() {
        return phoneEmailTypeSupervisorRepository.getAllType();
    }

    @Override
    public boolean supportsType(Class<?> type) {
        return type.equals(PhoneEmailTypeSupervisorDTO.class);
    }
}
