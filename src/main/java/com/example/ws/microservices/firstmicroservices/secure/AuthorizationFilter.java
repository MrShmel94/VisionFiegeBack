package com.example.ws.microservices.firstmicroservices.secure;

import com.example.ws.microservices.firstmicroservices.customError.AuthenticationFailedException;
import com.example.ws.microservices.firstmicroservices.customError.CustomException;
import com.example.ws.microservices.firstmicroservices.customError.InvalidTokenException;
import com.example.ws.microservices.firstmicroservices.customError.TooManyRequestsException;
import com.example.ws.microservices.firstmicroservices.service.RefreshTokenService;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import com.example.ws.microservices.firstmicroservices.utils.Utils;
import io.github.bucket4j.Bucket;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final Bucket bucket;
    private final RefreshTokenService refreshTokenService;
    private final Utils utils;
    private final UserService userService;

    public AuthorizationFilter(AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, Utils utils, UserService userService) {
        super(authenticationManager);

        this.refreshTokenService = refreshTokenService;
        this.utils = utils;
        this.userService = userService;

        this.bucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(200).refillGreedy(100, Duration.ofMinutes(1)))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {

            String uri = request.getRequestURI();
            System.out.println(uri);
            if (uri.equals(SecurityConstants.SIGN_UP_URL) || uri.equals("/swagger-ui/index.html")) {
                chain.doFilter(request, response);
                return;
            }

            String header = request.getHeader(SecurityConstants.HEADER_STRING);

            log.debug("Request URL: {}", request.getRequestURL());
            log.debug("Request Method: {}", request.getMethod());
            log.debug("Authorization Header: {}", header);

            if (!bucket.tryConsume(1)) {
                throw new TooManyRequestsException("Too many requests. Please try again later.");
            }

            if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
                log.warn("No JWT token found in request headers");
                chain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authentication = getAuthentication(header, request, response);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authentication successful for user: {}", authentication.getName());
            chain.doFilter(request, response);
        }catch (CustomException e){
            handleException(response, e);
        }
    }


    private UsernamePasswordAuthenticationToken getAuthentication(String header, HttpServletRequest request, HttpServletResponse response) {

        String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");

        try {
            Claims claims = utils.parseToken(token);
            String subject = claims.getSubject();

            String tokenIp = claims.get("ip", String.class);
            String tokenUserAgent = claims.get("user-agent", String.class);

            String requestIp = utils.getClientIp(request);
            String requestUserAgent = request.getHeader("User-Agent");

            if (!requestIp.equals(tokenIp) || !requestUserAgent.equals(tokenUserAgent)) {
                log.warn("Mismatch in IP or User-Agent. Possible token theft detected.");
                refreshTokenService.deleteTokensForUser(refreshTokenService.getSubjectFromToken(token));
                throw new InvalidTokenException("Invalid JWT token - mismatch in IP or User-Agent.");
            }

            if (subject == null) {
                throw new InvalidTokenException("Invalid JWT token - subject is null.");
            }

            return new UsernamePasswordAuthenticationToken(subject, null, new ArrayList<>());
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token, attempting to refresh...");
            return tryRefreshToken(request, response);
        } catch (JwtException e) {
            throw new AuthenticationFailedException("Invalid JWT token.");
        }

    }

    private UsernamePasswordAuthenticationToken tryRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken != null && refreshTokenService.validateRefreshToken(refreshToken)) {

            Claims claims = utils.parseToken(refreshToken);
            String userId = claims.getSubject();
            String tokenIp = claims.get("ip", String.class);
            String tokenUserAgent = claims.get("user-agent", String.class);

            String requestIp = utils.getClientIp(request);
            String requestUserAgent = request.getHeader("User-Agent");

            if (!requestIp.equals(tokenIp) || !requestUserAgent.equals(tokenUserAgent)) {
                log.warn("Mismatch in IP or User-Agent during refresh. Invalid refresh token.");
                refreshTokenService.deleteTokensForUser(userId);
                throw new AuthenticationFailedException("IP or User-Agent mismatch. Please log in again.");
            }

            String newAccessToken = utils.generateAccessToken(userId, request);

            response.setHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + newAccessToken);

//            CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsername(userId);
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Access token refreshed for user: {}", userId);

            return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        }

        log.warn("Failed to refresh access token. Invalid or expired refresh token.");
        throw new AuthenticationFailedException("Failed to refresh access token. Invalid or expired refresh token.");
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("RefreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void handleException(HttpServletResponse response, CustomException exception) throws IOException {
        response.setStatus(exception.getStatus().value());
        response.getWriter().write(exception.getErrorMessage());
        log.error("Authorization error: {}", exception.getErrorMessage());
    }
}
