package com.example.ws.microservices.firstmicroservices.kafka.listeners;

import com.example.ws.microservices.firstmicroservices.kafka.dto.EmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailListener {

    private final MailgunEmailSender sender;

    @Value("${DOMAIN_EMAIL_SEND}")
    private String sendDomainEmail;

    @EventListener
    public void handleEmailNotification(EmailNotification notification) {
        log.info("Processing email notification for email: {}", notification.getEmail());

        String verifyUrl = sendDomainEmail + "/api/v1/users/verify-email/" + notification.getToken();
        String html = loadTemplate().replace("{{VERIFY_URL}}", verifyUrl);

        sender.send(notification.getEmail(), "Verify Your Email – Fiege Vision", html);
    }

    private String loadTemplate() {
        try (InputStream in = getClass().getResourceAsStream("/message_template.html")) {
            return new String(Objects.requireNonNull(in).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load email template", e);
            return "<p>Click <a href='{{VERIFY_URL}}'>here</a> to verify</p>";
        }
    }
}
