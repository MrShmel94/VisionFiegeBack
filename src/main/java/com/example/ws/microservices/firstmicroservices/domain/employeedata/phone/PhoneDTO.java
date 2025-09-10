package com.example.ws.microservices.firstmicroservices.domain.employeedata.phone;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneDTO {
    private Integer id;
    private String number;
    private String type;
}
