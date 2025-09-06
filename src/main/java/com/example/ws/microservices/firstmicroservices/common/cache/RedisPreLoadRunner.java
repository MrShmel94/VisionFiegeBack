package com.example.ws.microservices.firstmicroservices.common.cache;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RedisPreLoadRunner {

    private final RedisTemplate<String, Object> redisTemplate;
    private final List<RedisPreLoader<?>> redisPreLoaders;

    @PostConstruct
    public void preLoad() {
        for (RedisPreLoader<?> service : redisPreLoaders) {
            service.preloadToCache(redisTemplate);
        }
    }
}
