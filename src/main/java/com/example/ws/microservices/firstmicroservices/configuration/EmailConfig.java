package com.example.ws.microservices.firstmicroservices.configuration;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Value("${mailgun.api-key}")
    private String apiKey;

    @Value("${mailgun.domain}")
    private String domain;

    @Value("${mailgun.from}")
    private String from;

    @Bean
    public MailgunProperties mailgunProperties() {
        return new MailgunProperties(apiKey, domain, from);
    }
}
