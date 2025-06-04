package com.example.ws.microservices.firstmicroservices.kafka.listeners;

import com.example.ws.microservices.firstmicroservices.configuration.MailgunProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailgunEmailSender {

    private final MailgunProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public void send(String to, String subject, String htmlBody) {
        String url = "https://api.eu.mailgun.net/v3/" + properties.domain() + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("api", properties.apiKey());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("from", properties.from());
        body.add("to", to);
        body.add("subject", subject);
        body.add("html", htmlBody);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
