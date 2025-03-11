package com.example.ws.microservices.firstmicroservices.request.attendance.gd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DayScheduleRequest {

    @NotBlank(message = "Status code is required")
    private String statusCode;

    @NotNull(message = "Shift id is required")
    private String shiftCode;

}
