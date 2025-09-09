package com.example.ws.microservices.firstmicroservices.common.mailing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationDTO {
    private String email;
    private String token;
}
