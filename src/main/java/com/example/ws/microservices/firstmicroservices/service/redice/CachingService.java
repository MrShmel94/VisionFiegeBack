package com.example.ws.microservices.firstmicroservices.service.redice;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public interface CachingService<T> {
    void preloadToCache(RedisTemplate<String, Object> redisTemplate);
    List<T> findAllWithSite();
    boolean supportsType(Class<?> type);
}
