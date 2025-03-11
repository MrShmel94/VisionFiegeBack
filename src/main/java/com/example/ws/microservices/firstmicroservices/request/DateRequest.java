package com.example.ws.microservices.firstmicroservices.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRequest {

    @Builder.Default
    private LocalDateTime validFrom = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime validTo = LocalDateTime.now().minusSeconds(1);

}
