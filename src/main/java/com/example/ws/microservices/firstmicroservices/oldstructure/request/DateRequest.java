package com.example.ws.microservices.firstmicroservices.oldstructure.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRequest {

    @Builder.Default
    private LocalDate validFrom = LocalDate.now();

    @Builder.Default
    private LocalDate validTo = null;

}
