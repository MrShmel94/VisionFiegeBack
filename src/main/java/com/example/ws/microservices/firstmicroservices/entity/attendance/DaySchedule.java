package com.example.ws.microservices.firstmicroservices.entity.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DaySchedule {
    private Integer shiftId;
    private Integer statusId;
}
