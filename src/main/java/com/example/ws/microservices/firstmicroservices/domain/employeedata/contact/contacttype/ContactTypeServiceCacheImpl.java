package com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.contacttype;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContactTypeServiceCacheImpl implements ContactTypeServiceCache {

    private final ContactTypeRepositoryCache contactTypeRepositoryCache;

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<ContactTypeDTO> allTypes = contactTypeRepositoryCache.getAllType();

        redisTemplate.opsForValue().set("typePhoneAndEmail", allTypes);

//        for (PhoneEmailTypeSupervisorDTO type : allTypes) {
//            redisTemplate.opsForValue().set("typePhoneAndEmail:" + type.getId(), type);
//        }
    }

    @Override
    public List<ContactTypeDTO> getAllFromDB() {
        return contactTypeRepositoryCache.getAllType();
    }

    @Override
    public boolean supportsType(Class<?> type) {
        return type.equals(ContactTypeDTO.class);
    }
}
