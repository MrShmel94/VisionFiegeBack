package com.example.ws.microservices.firstmicroservices.secure;

import com.example.ws.microservices.firstmicroservices.customError.AuthenticationFailedException;
import com.example.ws.microservices.firstmicroservices.customError.AuthenticationProcessingException;
import com.example.ws.microservices.firstmicroservices.customError.CustomException;
import com.example.ws.microservices.firstmicroservices.customError.InvalidLoginRequestException;
import com.example.ws.microservices.firstmicroservices.request.UserLoginRequestModel;
import com.example.ws.microservices.firstmicroservices.service.RefreshTokenService;
import com.example.ws.microservices.firstmicroservices.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final RefreshTokenService refreshTokenService;
    private final Utils utils;

    public AuthenticationFilter(AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, Utils utils) {
        super(authenticationManager);
        this.utils = utils;
        this.refreshTokenService = refreshTokenService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {

            log.debug("Attempting authentication for request: {}", req.getRequestURI());

            UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);
            log.debug("Authentication attempt with email: {}", creds.getEmail());

            if (creds.getEmail() == null || creds.getPassword() == null) {
                throw new InvalidLoginRequestException("Email or password not provided");
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    creds.getEmail(), creds.getPassword(), new ArrayList<>());

            try {
                return getAuthenticationManager().authenticate(authenticationToken);
            } catch (DisabledException e) {
                log.warn("User account is not verified: {}", creds.getEmail());
                throw new AuthenticationFailedException("Account not verified. Please check your email to verify your account.");
            }

        } catch (IOException e) {
            log.error("Failed to read user login request model", e);
            throw new InvalidLoginRequestException("Invalid login request data", e);
        } catch (Exception e) {
            log.error("Authentication process failed", e);
            throw new AuthenticationProcessingException("Authentication processing error", e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        log.error("Authentication failed: {}", failed.getMessage());

        try {
            if (failed instanceof DisabledException) {
                handleException(response, new AuthenticationFailedException("Account not verified. Please check your email to verify your account."));
            } else if (failed.getClass().getSimpleName().equals("BadCredentialsException")) {
                handleException(response, new InvalidLoginRequestException("Invalid credentials provided. Please try again."));
            } else {
                handleException(response, new AuthenticationFailedException("Authentication failed. Please check your credentials."));
            }
        } catch (IOException e) {
            log.error("Error writing the authentication failure response: {}", e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException {

        log.debug("Successful authentication initiated for request: {}", req.getRequestURI());

        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
            String accessToken = utils.generateAccessToken(customUserDetails.getUserId(), req);
            String refreshToken = refreshTokenService.createRefreshToken(customUserDetails.getUserId(), req);

            addResponseHeaders(res, accessToken, refreshToken);

            log.debug("Successful authentication for user: {}", customUserDetails.getUserId());
        } catch (Exception e) {
            log.error("Failed to process successful authentication", e);
            handleException(res, new AuthenticationProcessingException("Error generating tokens after authentication."));
        }
    }

    private void addResponseHeaders(HttpServletResponse res, String accessToken, String refreshToken) {
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + accessToken);

        res.addHeader("Set-Cookie", String.format(
                "RefreshToken=%s; Max-Age=%d; Path=/api/v1/auth/refresh; HttpOnly; Secure; SameSite=Strict",
                refreshToken, SecurityConstants.REFRESH_TOKEN_EXPIRATION / 1000
        ));

        log.debug("Added response headers and HTTP-only Secure Cookie for refresh token");
    }

    private void handleException(HttpServletResponse response, CustomException exception) throws IOException {
        response.setStatus(exception.getStatus().value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + exception.getErrorMessage() + "\"}");
        log.error("Authentication error: {}", exception.getErrorMessage());
    }

}
