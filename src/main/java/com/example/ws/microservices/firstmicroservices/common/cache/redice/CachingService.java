package com.example.ws.microservices.firstmicroservices.common.cache.redice;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public interface CachingService<T> {
    void preloadToCache(RedisTemplate<String, Object> redisTemplate);
    List<T> getAllFromDB();
    boolean supportsType(Class<?> type);
}
