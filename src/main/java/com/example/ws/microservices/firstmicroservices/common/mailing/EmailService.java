package com.example.ws.microservices.firstmicroservices.common.mailing;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
@Slf4j
public class EmailService {

    private final ApplicationEventPublisher publisher;

    public void publishEmailNotification(String email, String token) {
        EmailNotificationDTO notification = new EmailNotificationDTO(email, token);
        publisher.publishEvent(notification);
        log.info("Published email notification for email: {}", email);
    }
}
