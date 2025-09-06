package com.example.ws.microservices.firstmicroservices.domain.usermanagement;

import com.example.ws.microservices.firstmicroservices.common.errorhandling.customError.EmailCooldownException;
import com.example.ws.microservices.firstmicroservices.common.mailing.EmailService;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service.UserLookupService;
import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTokenService {

    private final RedisService redisService;
    private final UserLookupService userService;
    private final EmailService emailService;

    private static final String EMAIL_VERIFICATION_PREFIX = "email_verification:";
    private static final String EMAIL_LAST_SENT_PREFIX = "email_last_sent:";
    private static final Duration TTL = Duration.ofMinutes(15);
    private static final Duration COOLDOWN = Duration.ofMinutes(1);

    public void processEmailVerification(String email, @Nullable String tokenEmployee) {
        String lastSentKey = EMAIL_LAST_SENT_PREFIX + email;
        String lastSentTime = redisService.getFromCache(lastSentKey, String.class).orElse(null);

        if (lastSentTime != null && isWithinCooldown(lastSentTime)) {
            throw new EmailCooldownException("Please wait before requesting another email.");
        }

        String tokenKey = EMAIL_VERIFICATION_PREFIX + email;
        String token = redisService.getFromCache(tokenKey, String.class).orElseGet(() -> {
            String newToken = tokenEmployee;

            if (newToken == null) {
                newToken = userService.getUserByEmail(email).getEmailVerificationToken();
            }

            redisService.saveToCacheWithTTL(tokenKey, newToken, TTL);
            return newToken;
        });

        redisService.saveToCacheWithTTL(lastSentKey, LocalDateTime.now().toString(), COOLDOWN);
        emailService.publishEmailNotification(email, token);
    }

    private boolean isWithinCooldown(String lastSentTime) {
        LocalDateTime lastSent = LocalDateTime.parse(lastSentTime);
        return Duration.between(lastSent, LocalDateTime.now()).toMillis() < COOLDOWN.toMillis();
    }
}
