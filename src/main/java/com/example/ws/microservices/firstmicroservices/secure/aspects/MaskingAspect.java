package com.example.ws.microservices.firstmicroservices.secure.aspects;

import com.example.ws.microservices.firstmicroservices.dto.UserRoleDTO;
import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MaskingAspect {

    private final UserService userService;
    private final MaskFieldCache maskFieldCache;

    /**
     * Intercepts methods annotated with @MaskField and applies masking if needed.
     */
    @Around("@annotation(maskfield)")
    public Object maskFieldsIfNeeded(ProceedingJoinPoint joinPoint, MaskField maskfield) throws Throwable {
        Object result = joinPoint.proceed();

        if (result == null) return null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AccessDeniedException("Invalid authentication.");
        }

        SupervisorAllInformationDTO userInfo = userService.getSupervisorAllInformation(null, userDetails.getUsername());

        int maxWeight = userInfo.getRoles().stream()
                .mapToInt(UserRoleDTO::getWeight)
                .max()
                .orElse(0);

        if (result instanceof Optional<?> optional) {
            optional.ifPresent(obj -> maskObjectFields(obj, maxWeight));
        } else if (result instanceof List<?> list) {
            list.forEach(obj -> maskObjectFields(obj, maxWeight));
        } else {
            maskObjectFields(result, maxWeight);
        }

        return result;
    }

    /**
     * Applies masking to fields annotated with @MaskField if user weight is below the required threshold.
     */
    private void maskObjectFields(Object object, int userWeight) {
        if (object == null) return;

        List<Field> maskableFields = maskFieldCache.getMaskableFields(object.getClass());

        for (Field field : maskableFields) {
            MaskField maskField = field.getAnnotation(MaskField.class);
            if (userWeight < maskField.minWeight()) {
                try {
                    field.set(object, "****");
                    log.debug("Masked field: {} in class {}", field.getName(), object.getClass().getSimpleName());
                } catch (IllegalAccessException e) {
                    log.error("Error masking field: {}", field.getName(), e);
                }
            }
        }
    }
}
