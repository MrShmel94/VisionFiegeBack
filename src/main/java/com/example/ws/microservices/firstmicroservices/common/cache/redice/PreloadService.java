package com.example.ws.microservices.firstmicroservices.common.cache.redice;

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
