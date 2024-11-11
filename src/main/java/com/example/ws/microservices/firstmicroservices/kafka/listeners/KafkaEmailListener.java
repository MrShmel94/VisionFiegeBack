package com.example.ws.microservices.firstmicroservices.kafka.listeners;

import com.example.ws.microservices.firstmicroservices.kafka.dto.EmailNotification;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEmailListener {

    private final MailjetClient mailjetClient;

    @KafkaListener(topics = "email-notifications",
                   groupId = "email-verification-group",
                    containerFactory = "kafkaListenerContainerFactory")
    public void handleEmailNotification(EmailNotification notification) {
        log.info("Processing email notification for email: {}", notification.getEmail());
        sendEmail(notification.getEmail(), notification.getToken());
    }

    private void sendEmail(String email, String token) {
        String subject = "Verify Your Email - Your Company Name";
        String verifyURL = "http://localhost:8080/api/v1/users/verify?code=" + token;

        String mailContent = "<p>Dear User,</p>"
                + "<p>Welcome to <strong>Your Company Name</strong>!</p>"
                + "<p>To activate your account, confirm your email by clicking the link below:</p>"
                + "<p style='text-align: center;'><a href=\"" + verifyURL + "\">Verify Your Email</a></p>";

        try {
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", "your-email@example.com")
                                            .put("Name", "Your Company"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject().put("Email", email)))
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(Emailv31.Message.HTMLPART, mailContent)));
            MailjetResponse response = mailjetClient.post(request);

            if (response.getStatus() != 200) {
                log.error("Failed to send email: {}", response.getStatus());
            }
        } catch (MailjetException e) {
            log.error("Error while sending email to {}: {}", email, e.getMessage());
        }
    }
}
