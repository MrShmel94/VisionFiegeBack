package com.example.ws.microservices.firstmicroservices.secure;

import com.example.ws.microservices.firstmicroservices.customError.AuthenticationProcessingException;
import com.example.ws.microservices.firstmicroservices.customError.InvalidLoginRequestException;
import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.request.UserLoginRequestModel;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final SecretKeyProvider secretKeyProvider;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, SecretKeyProvider secretKeyProvider) {
        super(authenticationManager);
        this.userService = userService;
        this.secretKeyProvider = secretKeyProvider;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {

            log.info("Attempting authentication for request: {}", req.getRequestURI());

            UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);
            log.info("Authentication attempt with email: {}", creds.getEmail());

            if (creds.getEmail() == null || creds.getPassword() == null) {
                throw new InvalidLoginRequestException("Email or password not provided");
            }

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            log.error("Failed to read user login request model", e);
            throw new InvalidLoginRequestException("Invalid login request data", e);
        } catch (Exception e) {
            log.error("Authentication process failed", e);
            throw new AuthenticationProcessingException("Authentication processing error", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth){

        log.info("Successful authentication initiated for request: {}", req.getRequestURI());

        try {
            String userName = ((User) auth.getPrincipal()).getUsername();

            String token = generateToken(userName);

            UserDto userDto = userService.getUser(userName);

            addResponseHeaders(res, token, userDto.getUserId());

            log.info("Successful authentication for user: {}", userName);
        } catch (Exception e) {
            log.error("Failed to process successful authentication", e);
        }
    }

    private String generateToken(String userName) {
        SecretKey secretKey = secretKeyProvider.getSecretKey();

        Instant now = Instant.now();

        String token = Jwts.builder()
                .setSubject(userName)
                .setExpiration(Date.from(now.plusMillis(SecurityConstants.EXPIRATION_TIME)))
                .setIssuedAt(Date.from(now))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        log.debug("Generated JWT token for user: {}", userName);

        return token;
    }

    private void addResponseHeaders(HttpServletResponse res, String token, String userId) {
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        res.addHeader("UserId", userId);

        log.debug("Added response headers for userId: {}", userId);
    }

}
