package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.secure.SecurityConstants;
import com.example.ws.microservices.firstmicroservices.service.RefreshTokenService;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import com.example.ws.microservices.firstmicroservices.utils.Utils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final Utils utils;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "RefreshToken",  required = false) String refreshToken, HttpServletRequest request) {

        if (refreshToken == null) {
            log.warn("No refresh token found in request cookies");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing.");
        }

        try {
            if (refreshTokenService.validateRefreshToken(refreshToken)) {

                Claims claims = utils.parseToken(refreshToken);
                String requestIp = request.getRemoteAddr();
                String tokenIp = claims.get("ip", String.class);

                if (!requestIp.equals(tokenIp)) {
                    refreshTokenService.deleteTokensForUser(claims.getSubject());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your session has been terminated due to IP change. Please log in again.");
                }

                String userId = refreshTokenService.getSubjectFromToken(refreshToken);
                String newAccessToken = utils.generateAccessToken(userId, request);

                CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsername(userId);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return ResponseEntity.ok()
                        .header(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + newAccessToken)
                        .build();
            } else {
                log.warn("Invalid or expired refresh token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token.");
            }
        } catch (Exception e) {
            log.error("Error refreshing access token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("RefreshToken") String refreshToken) {
        try {
            if (refreshTokenService.validateRefreshToken(refreshToken)) {
                refreshTokenService.revokeRefreshToken(refreshToken);
                return ResponseEntity.ok("Logged out successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token.");
            }
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during logout.");
        }
    }
}
