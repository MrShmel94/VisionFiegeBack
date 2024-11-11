package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.kafka.dto.EmailNotification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
@Slf4j
public class EmailService {

    private final KafkaTemplate<String, EmailNotification> kafkaTemplate;
    private static final String EMAIL_TOPIC = "email-notifications";

    public void publishEmailNotification(String email, String token) {
        EmailNotification notification = new EmailNotification(email, token);
        kafkaTemplate.send(EMAIL_TOPIC, notification);
        log.info("Published email notification to Kafka for email: {}", email);
    }
}
