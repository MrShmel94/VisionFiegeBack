package com.example.ws.microservices.firstmicroservices.common.utils;

import com.example.ws.microservices.firstmicroservices.secure.SecretKeyProvider;
import com.example.ws.microservices.firstmicroservices.secure.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Random;

@Component
@AllArgsConstructor
public class Utils {

    private final SecretKeyProvider secretKeyProvider;
    private final Random RANDOM = new SecureRandom();

    public String generateUserId(int length){
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder();

        for( int i = 0; i < length; i++ ){
            String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }

    public String generateAccessToken(String userId, HttpServletRequest request) {
        SecretKey secretKey = secretKeyProvider.getSecretKey();
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(Date.from(now.plusMillis(SecurityConstants.ACCESS_TOKEN_EXPIRATION)))
                .setIssuedAt(Date.from(now))
                .claim("ip", getClientIp(request))
                .claim("user-agent", request.getHeader("User-Agent"))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String userId, HttpServletRequest request) {
        SecretKey secretKey = secretKeyProvider.getSecretKey();
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(Date.from(now.plusMillis(SecurityConstants.REFRESH_TOKEN_EXPIRATION)))
                .setIssuedAt(Date.from(now))
                .setId(java.util.UUID.randomUUID().toString())
                .claim("ip", getClientIp(request))
                .claim("user-agent", request.getHeader("User-Agent"))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    public Claims parseToken(String token) {
        SecretKey secretKey = secretKeyProvider.getSecretKey();
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
