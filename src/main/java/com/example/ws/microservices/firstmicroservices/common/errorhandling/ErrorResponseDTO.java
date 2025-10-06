package com.example.ws.microservices.firstmicroservices.common.errorhandling;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ErrorResponseDTO(
        String code,
        String message,
        int status,
        Instant timestamp,
        Map<String, List<String>> details
) {}
