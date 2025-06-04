package com.example.ws.microservices.firstmicroservices.request.attendance.gd;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DayScheduleRequest {

    @NotNull(message = "Status id is required")
    @JsonInclude()
    private String statusCode;

    @NotNull(message = "Shift id is required")
    private String shiftCode;

}
