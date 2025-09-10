package com.example.ws.microservices.firstmicroservices.domain.usermanagement.refreshtoken;

import com.example.ws.microservices.firstmicroservices.common.security.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.common.security.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenService refreshTokenService;

    @Value("${COOKIE_DOMAIN:}")
    private String cookieDomain;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUserId();
                refreshTokenService.deleteTokensForUser(userId);
            }

                ResponseCookie clearAccess = ResponseCookie.from("AccessToken", "")
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .domain(cookieDomain)
                        .sameSite("Strict")
                        .maxAge(0)
                        .build();

                ResponseCookie clearRefresh = ResponseCookie.from("RefreshToken", "")
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .domain(cookieDomain)
                        .sameSite("Strict")
                        .maxAge(0)
                        .build();

                response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());

                SecurityContextHolder.clearContext();

                return ResponseEntity.ok("Logged out successfully.");

        } catch (Exception e) {
            log.error("Error during logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred during logout.");
        }
    }
}
