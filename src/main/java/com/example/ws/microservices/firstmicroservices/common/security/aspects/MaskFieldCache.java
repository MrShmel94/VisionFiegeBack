package com.example.ws.microservices.firstmicroservices.common.security.aspects;


import com.example.ws.microservices.firstmicroservices.common.security.annotations.MaskField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MaskFieldCache {

    private final Map<Class<?>, List<Field>> cache = new ConcurrentHashMap<>();

    /**
     * Retrieves cached fields with @MaskField annotation for a given class.
     * If not cached, computes and stores them.
     */
    public List<Field> getMaskableFields(Class<?> clazz) {
        return cache.computeIfAbsent(clazz, c -> {
            List<Field> fields = new ArrayList<>();
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(MaskField.class)) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
            log.info("Cached {} maskable fields for class {}", fields.size(), clazz.getSimpleName());
            return fields;
        });
    }
}