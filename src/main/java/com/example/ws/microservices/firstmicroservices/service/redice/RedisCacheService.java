package com.example.ws.microservices.firstmicroservices.service.redice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private List<CachingService<?>> cachingServices;

    public void updateSupervisorAccess(String supervisorExpertis, List<String> employeeExpertis) {
        String key = "supervisor:" + supervisorExpertis;
        redisTemplate.opsForList().rightPushAll(key, employeeExpertis);
        log.info("Updated Redis for supervisor {} with employees: {}", supervisorExpertis, employeeExpertis);
    }

    public void removeExpiredAccess(String supervisorExpertis, String employeeExpertis) {
        String key = "supervisor:" + supervisorExpertis;
        redisTemplate.opsForList().remove(key, 1, employeeExpertis);
        log.info("Removed expired access for supervisor {} and employee {}", supervisorExpertis, employeeExpertis);
    }

    public <T> void saveToCache(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> Optional<T> getFromCache(String key, Class<T> type) {
        T value = (T) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    public <T> void saveToCacheWithTTL(String key, T value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public void removeFromCache(String key) {
        redisTemplate.delete(key);
    }

    public boolean isKeyInCache(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public <T> List<T> getAllFromCache(String keyPattern, Class<T> type) {
        Set<String> keys = redisTemplate.keys(keyPattern);

        if (keys == null || keys.isEmpty()) {
            log.info("No keys found for pattern: {}. Attempting to reload cache.", keyPattern);

            cachingServices.stream()
                    .filter(service -> service.supportsType(type))
                    .findFirst()
                    .ifPresentOrElse(
                            service -> service.preloadToCache(redisTemplate),
                            () -> log.warn("No caching service found for type: {}", type.getSimpleName())
                    );

            keys = redisTemplate.keys(keyPattern);
            if (keys == null || keys.isEmpty()) {
                log.error("Failed to preload cache for pattern: {}", keyPattern);
                return Collections.emptyList();
            }
        }

        List<Object> rawValues = redisTemplate.opsForValue().multiGet(keys);
        if (rawValues == null) {
            return Collections.emptyList();
        }

        return rawValues.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }
}
