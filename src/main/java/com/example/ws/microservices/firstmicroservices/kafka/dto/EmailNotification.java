package com.example.ws.microservices.firstmicroservices.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotification {
    private String email;
    private String token;
}
