package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.customError.EmailCooldownException;
import com.example.ws.microservices.firstmicroservices.service.UserLookupService;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTokenService {

    private final RedisCacheService redisCacheService;
    private final UserLookupService userService;
    private final EmailService emailService;

    private static final String EMAIL_VERIFICATION_PREFIX = "email_verification:";
    private static final String EMAIL_LAST_SENT_PREFIX = "email_last_sent:";
    private static final Duration TTL = Duration.ofMinutes(15);
    private static final Duration COOLDOWN = Duration.ofMinutes(1);

    public void processEmailVerification(String email) {
        String lastSentKey = EMAIL_LAST_SENT_PREFIX + email;
        String lastSentTime = redisCacheService.getFromCache(lastSentKey, String.class).orElse(null);

        if (lastSentTime != null && isWithinCooldown(lastSentTime)) {
            throw new EmailCooldownException("Please wait before requesting another email.");
        }

        String tokenKey = EMAIL_VERIFICATION_PREFIX + email;
        String token = redisCacheService.getFromCache(tokenKey, String.class).orElseGet(() -> {
            String newToken = userService.getUserByEmail(email).getEmailVerificationToken();
            redisCacheService.saveToCacheWithTTL(tokenKey, newToken, TTL);
            return newToken;
        });

        redisCacheService.saveToCacheWithTTL(lastSentKey, LocalDateTime.now().toString(), COOLDOWN);
        emailService.publishEmailNotification(email, token);
    }

    private boolean isWithinCooldown(String lastSentTime) {
        LocalDateTime lastSent = LocalDateTime.parse(lastSentTime);
        return Duration.between(lastSent, LocalDateTime.now()).toMillis() < COOLDOWN.toMillis();
    }
}
