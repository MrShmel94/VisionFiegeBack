package com.example.ws.microservices.firstmicroservices.secure;

import com.example.ws.microservices.firstmicroservices.customError.AuthenticationFailedException;
import com.example.ws.microservices.firstmicroservices.customError.InvalidTokenException;
import com.example.ws.microservices.firstmicroservices.customError.TooManyRequestsException;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.refreshtoken.RefreshTokenService;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service.UserService;
import com.example.ws.microservices.firstmicroservices.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.bucket4j.Bucket;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.ws.microservices.firstmicroservices.secure.SecurityConstants.*;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

/**
 * AuthorizationFilter handles user authentication by validating JWT access tokens and refreshing them
 * if necessary. It ensures that requests are authorized and prevents potential token theft by validating
 * IP addresses headers.
 * <p>
 * This filter also implements rate limiting to protect against DDoS attacks.
 */

@Slf4j
public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final ConcurrentHashMap<String, BucketWrapper> buckets = new ConcurrentHashMap<>();
    private final RefreshTokenService refreshTokenService;
    private final Utils utils;
    private final UserService userService;
    private final String COOKIE_DOMAIN;

    public AuthorizationFilter(AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, Utils utils, UserService userService, String COOKIE_DOMAIN) {
        super(authenticationManager);

        this.refreshTokenService = refreshTokenService;
        this.utils = utils;
        this.userService = userService;
        this.COOKIE_DOMAIN = COOKIE_DOMAIN;
    }

    /**
     * Applies authentication checks and rate limiting to incoming requests.
     * <p>
     * 1. Allows unauthenticated access to specific URIs such as sign-up and Swagger UI.
     * 2. Implements rate limiting per user or IP using Bucket4j.
     * 3. Extracts the JWT access token from cookies and validates it.
     * 4. Sets the security context with the authenticated user's details.
     *
     * @param request  the HTTP request.
     * @param response the HTTP response.
     * @param chain    the filter chain.
     * @throws IOException      if an input/output error occurs.
     * @throws ServletException if a servlet error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String uri = request.getRequestURI();

            if (isPublicUri(uri)) {
                chain.doFilter(request, response);
                return;
            }

            String requestIp = utils.getClientIp(request);
            Bucket bucket = resolveBucket(requestIp);

            if (!bucket.tryConsume(1)) {
                throw new TooManyRequestsException("Too many requests. Please try again later.");
            }

            String accessToken = getCookieValue(request, "AccessToken");

            if (accessToken == null) {
                throw new InvalidTokenException("Access token is missing.");
            }

            UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            } else {
                throw new InvalidTokenException("Invalid access token.");
            }

        } catch (InvalidTokenException | AuthenticationFailedException ex) {
            log.info("Authentication error: {}", ex.getMessage());

            clearCookies(response);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", ex.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.writeValue(response.getWriter(), errorResponse);
        }
    }

    /**
     * Extracts and validates the JWT access token from the cookies.
     * <p>
     * 1. Parses the token and validates the user details, IP address.
     * 2. Deletes tokens if a mismatch in IP is detected to prevent token theft.
     * 3. Refreshes the token if it has expired, by calling `tryRefreshToken`.
     *
     * @param request  the HTTP request containing the cookies.
     * @param response the HTTP response to send updated tokens if needed.
     * @return an authenticated user wrapped in a UsernamePasswordAuthenticationToken, or null if no token is found.
     * @throws InvalidTokenException         if the token has invalid details or is tampered with.
     * @throws AuthenticationFailedException if the token is invalid or cannot be refreshed.
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = getCookieValue(request, "AccessToken");

        if (accessToken == null) {
            log.warn("Access token not found in cookies.");
            return null;
        }

        try {
            Claims claims = utils.parseToken(accessToken);

            if (claims == null) {
                throw new InvalidTokenException("Invalid JWT token - claims are null.");
            }

            String userId = claims.getSubject();
            String tokenIp = claims.get("ip", String.class);
            String requestIp = utils.getClientIp(request);

            if (!requestIp.equals(tokenIp)) {
                log.warn("IP mismatch detected for userId: {}. Invalidating all tokens.", userId);
                refreshTokenService.deleteTokensForUser(userId);
                throw new InvalidTokenException("Invalid JWT token - mismatch in IP");
            }

            if (userId == null) {
                throw new InvalidTokenException("Invalid JWT token - subject is null.");
            }

            CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsernameWithoutPassword(userId);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token, attempting to refresh...");
            return tryRefreshToken(request, response);
        } catch (JwtException e) {
            throw new AuthenticationFailedException("Invalid JWT token.");
        }
    }


    /**
     * Attempts to refresh the user's access token using a valid refresh token.
     * <p>
     * 1. Retrieves the refresh token from cookies and validates it.
     * 2. Parses the refresh token to extract claims and the user's IP address.
     * 3. Compares the request IP with the token's IP to detect potential misuse.
     * 4. Generates a new access token upon successful validation and sets it as a secure cookie.
     *
     * @param request  the HTTP request containing the refresh token.
     * @param response the HTTP response to set the new access token.
     * @return an authenticated user wrapped in a UsernamePasswordAuthenticationToken.
     * @throws AuthenticationFailedException if the refresh token is invalid, expired, or there is an IP mismatch.
     */
    private UsernamePasswordAuthenticationToken tryRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request, "RefreshToken");
        if (refreshToken != null && refreshTokenService.validateRefreshToken(refreshToken)) {
            Claims claims = utils.parseToken(refreshToken);
            String userId = claims.getSubject();

            String tokenIp = claims.get("ip", String.class);
            String requestIp = utils.getClientIp(request);

            if (!requestIp.equals(tokenIp)) {
                log.warn("IP mismatch detected during token refresh for userId: {}", userId);
                refreshTokenService.deleteTokensForUser(userId);
                throw new AuthenticationFailedException("IP mismatch. Please log in again.");
            }

            String newAccessToken = utils.generateAccessToken(userId, request);
            ResponseCookie accessCookie = ResponseCookie.from("AccessToken", newAccessToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .domain(COOKIE_DOMAIN)
                    .sameSite("Strict")
                    .maxAge(SecurityConstants.ACCESS_TOKEN_EXPIRATION)
                    .build();

            response.addHeader(SET_COOKIE, accessCookie.toString());

            log.info("Access token successfully refreshed for userId: {}", userId);

            CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsername(userId);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

        throw new AuthenticationFailedException("Failed to refresh access token. Invalid or expired refresh token.");
    }


    /**
     * Retrieves the value of a specific cookie by its name.
     * <p>
     * Iterates through the cookies in the request to find the specified cookie name.
     * Returns null if the cookie is not found or if there are no cookies.
     *
     * @param request    the HTTP request containing cookies.
     * @param cookieName the name of the cookie to retrieve.
     * @return the value of the specified cookie, or null if not found.
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            log.debug("No cookies found in the request.");
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        log.debug("Cookie {} not found in the request.", cookieName);
        return null;
    }


    /**
     * Checks if the URI is public and does not require authentication.
     *
     * @param uri the URI of the incoming request.
     * @return true if the URI is public, false otherwise.
     */
    private boolean isPublicUri(String uri) {
        return uri.equals(SIGN_UP_URL)
                || uri.equals("/swagger-ui/index.html")
                || uri.contains("/api/v1/users/verify-email/")
                || uri.startsWith("/actuator")
                || uri.contains("/favicon.ico");
    }


    /**
     * Resolves or creates a rate-limiting bucket for a given key (e.g., user IP or user ID).
     * <p>
     * Each key is associated with a separate Bucket instance to allow granular rate limiting.
     * The bucket enforces the following rate-limiting rules:
     * 1. Capacity: A maximum of 200 requests can be stored in the bucket at any given time.
     * 2. Refill: 100 tokens are added to the bucket every 1 minute.
     * <p>
     * If the bucket for the given key does not exist, it is created with the specified limits.
     *
     * @param key a unique identifier for the bucket (e.g., IP address or user ID).
     * @return a Bucket instance associated with the given key.
     */
    private Bucket resolveBucket(String key) {
        BucketWrapper wrapper = buckets.computeIfAbsent(key, k -> new BucketWrapper(
                Bucket.builder()
                        .addLimit(limit -> limit.capacity(BUCKET_CAPACITY).refillGreedy(BUCKET_REFILL_TOKENS, Duration.ofMinutes(1)))
                        .build()
        ));

        wrapper.updateLastAccessed();
        return wrapper.getBucket();
    }

    /**
     * Cleans up unused or inactive buckets from the map.
     * <p>
     * Buckets are considered inactive if their available tokens match the full capacity,
     * meaning no requests have been made recently.
     * This method helps prevent memory bloat caused by unused keys.
     * <p>
     * Handles periodic cleanup of inactive buckets.
     * <p>
     * 1. Removes buckets that have not been accessed for a configurable duration (e.g., 30 minutes).
     * 2. Uses the BucketWrapper's lastAccessed timestamp to determine inactivity.
     * 3. Scheduled to run every 10 minutes using Spring's @Scheduled annotation.
     * <p>
     * Benefits:
     * - Prevents memory bloat caused by unused buckets.
     * - Ensures active users are not affected by the cleanup process.
     * <p>
     * Thread-Safety:
     * The use of ConcurrentHashMap ensures safe concurrent access during cleanup and bucket usage.
     */
    @Scheduled(fixedRate = 600000)
    private void cleanupBuckets() {
        long expirationTime = System.currentTimeMillis() - Duration.ofMinutes(INACTIVITY_CLEANUP_MINUTES).toMillis();
        buckets.entrySet().removeIf(entry -> entry.getValue().getLastAccessed() < expirationTime);
        log.info("Cleanup completed. Active buckets: {}", buckets.size());
    }

    private void clearCookies(HttpServletResponse response) {
        ResponseCookie clearAccess = ResponseCookie.from("AccessToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(COOKIE_DOMAIN)
                .sameSite("Strict")
                .maxAge(0)
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("RefreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(COOKIE_DOMAIN)
                .sameSite("Strict")
                .maxAge(0)
                .build();

        response.addHeader(SET_COOKIE, clearAccess.toString());
        response.addHeader(SET_COOKIE, clearRefresh.toString());

        log.debug("Cleared AccessToken and RefreshToken cookies");
    }

}
