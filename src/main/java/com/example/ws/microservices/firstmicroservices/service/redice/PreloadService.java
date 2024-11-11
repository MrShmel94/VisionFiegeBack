package com.example.ws.microservices.firstmicroservices.service.redice;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.*;
import com.example.ws.microservices.firstmicroservices.service.templateTables.*;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PreloadService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final List<CachingService<?>> cachingServices;

    @PostConstruct
    public void preloadCache() {
        for (CachingService<?> service : cachingServices) {
            service.preloadToCache(redisTemplate);
        }
    }
}
