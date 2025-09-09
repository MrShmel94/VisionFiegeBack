package com.example.ws.microservices.firstmicroservices.common.cache.redice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private List<CachingService<?>> cachingServices;
    private final ObjectMapper objectMapper;

    public void updateSupervisorAccess(String supervisorExpertis, List<String> employeeExpertis) {
        String key = "supervisor:" + supervisorExpertis;
        redisTemplate.opsForList().rightPushAll(key, employeeExpertis);
        log.info("Updated Redis for supervisor {} with employees: {}", supervisorExpertis, employeeExpertis);
    }

    public <T> void saveToCache(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void saveMapping(String userId, String expertis) {
        redisTemplate.opsForHash().put("userMappings", userId, expertis);
    }

    public <T> void saveMapping(String mapping, String expertis, T object) {
        redisTemplate.opsForHash().put(mapping, expertis, object);
    }

    public void saveAllMapping(String redisKey, Map<String, ?> values) {
        redisTemplate.opsForHash().putAll(redisKey, values);
    }

    public <T>Optional<T> getValueFromMapping(String mapping, String expertis, TypeReference<T> typeReference) {
        Object cachedValue = redisTemplate.opsForHash().get(mapping, expertis);
        if (cachedValue == null) {
            return Optional.empty();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            T value = mapper.convertValue(cachedValue, typeReference);
            return Optional.of(value);
        } catch (IllegalArgumentException e) {
            log.error("Error converting cached value: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void removeEmployeeFromMapping(String expertis) {
        redisTemplate.opsForHash().delete("userFullMapping", expertis);
    }

    public <T>Optional<T> getValueFromMapping(String mapping, String expertis, Class<T> type) {
        Object cachedValue = redisTemplate.opsForHash().get(mapping, expertis);

        if (cachedValue == null) {
            return Optional.empty();
        }

        if (type.isInstance(cachedValue)) {
            return Optional.of(type.cast(cachedValue));
        }

        if (cachedValue instanceof LinkedHashMap) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            try {
                T value = mapper.convertValue(cachedValue, type);
                return Optional.of(value);
            } catch (IllegalArgumentException e) {
                log.error("Error converting cached value to type {}: {}", type.getSimpleName(), e.getMessage());
            }
        }
        return Optional.empty();
    }

    public <T> Map<String, T> getEmployeeFullMapping(Collection<String> expertisList, Class<T> clazz){
        List<Object> cachedValues = redisTemplate.opsForHash().multiGet("userFullMapping", new ArrayList<>(expertisList));

        Map<String, T> result = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        int i = 0;
        for (String expertis : expertisList) {
            Object cachedValue = cachedValues.get(i++);
            if (cachedValue != null) {
                if (clazz.isInstance(cachedValue)) {
                    result.put(expertis, clazz.cast(cachedValue));
                } else if (cachedValue instanceof LinkedHashMap) {
                    try {
                        T value = mapper.convertValue(cachedValue, clazz);
                        result.put(expertis, value);
                    } catch (IllegalArgumentException e) {
                        log.error("Error converting cached value for {}: {}", expertis, e.getMessage());
                    }
                }
            }
        }
        return result;
    }

    public String getExpertisByUserId(String userId) {
        return (String) redisTemplate.opsForHash().get("userMappings", userId);
    }

    public <T> Optional<T> getFromCache(String key, TypeReference<T> typeReference) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue == null) {
            return Optional.empty();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            T value = mapper.convertValue(cachedValue, typeReference);
            return Optional.of(value);
        } catch (IllegalArgumentException e) {
            log.error("Error converting cached value: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public <T> Optional<T> getFromCache(String key, Class<T> type) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue == null) {
            return Optional.empty();
        }

        if (type.isInstance(cachedValue)) {
            return Optional.of(type.cast(cachedValue));
        }

        if (cachedValue instanceof LinkedHashMap) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            try {
                T value = mapper.convertValue(cachedValue, type);
                return Optional.of(value);
            } catch (IllegalArgumentException e) {
                log.error("Error converting cached value to type {}: {}", type.getSimpleName(), e.getMessage());
            }
        }

        return Optional.empty();
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return rawValues.stream()
                .filter(Objects::nonNull)
                .map(rawValue -> mapper.convertValue(rawValue, type))
                .toList();
    }

    public <T> Map<String, T> getMultiFromCacheAsMap(List<String> keys, Class<T> type) {
        List<Object> values = redisTemplate.opsForValue().multiGet(
                keys.stream().map(key -> "employee:" + key).toList()
        );

        if (values == null) {
            log.warn("No values found in Redis for keys: {}", keys);
            return Collections.emptyMap();
        }

        return IntStream.range(0, Math.min(keys.size(), values.size()))
                .filter(i -> type.isInstance(values.get(i)))
                .boxed()
                .collect(Collectors.toMap(
                        keys::get,
                        i -> type.cast(values.get(i)),
                        (v1, v2) -> v1
                ));
    }


    //WORK ON HSET
    public <T> void saveToHash(String hashName, String fieldKey, T value) {
        redisTemplate.opsForHash().put(hashName, fieldKey, value);
    }

    public <T> void saveAllToHash(String hashName, Map<String, T> values) {
        redisTemplate.opsForHash().putAll(hashName, values);
    }

    public <T> Optional<T> getFromHash(String hashName, String fieldKey, Class<T> type) {
        Object cachedValue = redisTemplate.opsForHash().get(hashName, fieldKey);
        if (cachedValue == null) {
            return Optional.empty();
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            T value = mapper.convertValue(cachedValue, type);
            return Optional.of(value);
        } catch (IllegalArgumentException e) {
            log.error("Error converting value from hash {} field {}: {}", hashName, fieldKey, e.getMessage());
            return Optional.empty();
        }
    }

    public <T> Map<String, T> getAllFromHash(String hashName, Class<T> type) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashName);
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyMap();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Map<String, T> result = new HashMap<>();
        entries.forEach((key, value) -> {
            try {
                T convertedValue = mapper.convertValue(value, type);
                result.put((String) key, convertedValue);
            } catch (IllegalArgumentException e) {
                log.error("Error converting value from hash {} key {}: {}", hashName, key, e.getMessage());
            }
        });

        return result;
    }

    public void deleteFromHash(String hashName, String fieldKey) {
        redisTemplate.opsForHash().delete(hashName, fieldKey);
    }

    public void deleteMultipleFromHash(String hashName, List<String> fieldKeys) {
        if (fieldKeys.isEmpty()) return;
        redisTemplate.opsForHash().delete(hashName, fieldKeys.toArray(new Object[0]));
    }

    public <T> Optional<T> getFromHash(String hashName, String fieldKey, TypeReference<T> typeReference) {
        Object cachedValue = redisTemplate.opsForHash().get(hashName, fieldKey);
        if (cachedValue == null) {
            return Optional.empty();
        }
        try {
            T value = objectMapper.convertValue(cachedValue, typeReference);
            return Optional.of(value);
        } catch (IllegalArgumentException e) {
            log.error("Error converting value from hash {} field {}: {}", hashName, fieldKey, e.getMessage());
            return Optional.empty();
        }
    }

    public <T> Map<String, T> getAllFromHash(String hashName, TypeReference<T> typeReference) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashName);
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, T> result = new HashMap<>();
        entries.forEach((key, value) -> {
            try {
                T convertedValue = objectMapper.convertValue(value, typeReference);
                result.put((String) key, convertedValue);
            } catch (IllegalArgumentException e) {
                log.error("Error converting value from hash {} key {}: {}", hashName, key, e.getMessage());
            }
        });

        return result;
    }
}
