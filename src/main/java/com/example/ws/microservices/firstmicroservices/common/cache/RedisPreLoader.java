package com.example.ws.microservices.firstmicroservices.common.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public interface RedisPreLoader<T> {
    void preloadToCache(RedisTemplate<String, Object> redisTemplate);
    List<T> getAllFromDB();
    boolean supportsType(Class<?> type);
}
