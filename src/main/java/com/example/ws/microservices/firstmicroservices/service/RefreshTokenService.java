package com.example.ws.microservices.firstmicroservices.service;

import jakarta.servlet.http.HttpServletRequest;

public interface RefreshTokenService {

    String createRefreshToken(String userId, HttpServletRequest request);
    boolean validateRefreshToken(String token);
    void revokeRefreshToken(String token);
    void deleteTokensForUser(String userId);
    String getSubjectFromToken(String token);
}
