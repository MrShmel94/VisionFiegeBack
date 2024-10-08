package com.example.ws.microservices.firstmicroservices.secure;

import io.github.bucket4j.Bucket;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final Bucket bucket;
    private final SecretKeyProvider secretKeyProvider;

    public AuthorizationFilter(AuthenticationManager authenticationManager, SecretKeyProvider secretKeyProvider) {
        super(authenticationManager);

        this.secretKeyProvider = secretKeyProvider;

        this.bucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(200).refillGreedy(100, Duration.ofMinutes(1)))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        log.info("Request URL: {}", request.getRequestURL());
        log.info("Request Method: {}", request.getMethod());
        log.info("Authorization Header: {}", header);

        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for request: {}", request.getRequestURL());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Too many requests");
            return;
        }

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            log.info("No JWT token found in request headers");
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(header);

        if (authentication == null) {
            log.info("JWT token is missing or invalid");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authentication successful for user: {}", authentication.getName());
        chain.doFilter(request, response);
    }


    private UsernamePasswordAuthenticationToken getAuthentication(String header) {

        String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");
        SecretKey secretKey = secretKeyProvider.getSecretKey();
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        Jws<Claims> jwt = jwtParser.parseClaimsJws(token);
        String subject = jwt.getBody().getSubject();

        if (subject == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());

    }


}
