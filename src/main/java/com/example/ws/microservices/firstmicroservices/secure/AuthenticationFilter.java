package com.example.ws.microservices.firstmicroservices.secure;

import com.example.ws.microservices.firstmicroservices.customError.*;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserMeDTO;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserLoginRequestModel;
import com.example.ws.microservices.firstmicroservices.service.RefreshTokenService;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.UserService;
import com.example.ws.microservices.firstmicroservices.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final RefreshTokenService refreshTokenService;
    private final Utils utils;
    private final String COOKIE_DOMAIN;
    private final UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, Utils utils, String COOKIE_DOMAIN, UserService userService) {
        super(authenticationManager);
        this.utils = utils;
        this.COOKIE_DOMAIN = COOKIE_DOMAIN;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }


    /**
     * Attempts to authenticate a user based on provided credentials in the request.
     * <p>
     * 1. Reads and parses the `UserLoginRequestModel` from the request input stream.
     * 2. Validates the email and password fields for null values.
     * 3. Creates an `UsernamePasswordAuthenticationToken` and delegates to the AuthenticationManager for validation.
     * 4. Handles specific authentication exceptions, such as account not verified (`DisabledException`).
     *
     * @param req the HTTP request containing user credentials.
     * @param res the HTTP response.
     * @return an `Authentication` object if authentication is successful.
     * @throws InvalidLoginRequestException if credentials are missing or invalid.
     * @throws CustomAuthenticationException if account is not verified or another authentication error occurs.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {

            log.debug("Attempting authentication for request: {}", req.getRequestURI());

            UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);
            log.debug("Authentication attempt with email: {}", creds.getEmail());

            if (creds.getEmail() == null || creds.getEmail().trim().isEmpty()) {
                sendErrorResponse(res, HttpStatus.BAD_REQUEST,
                        "Email is required",
                        "Please check your email.");
                return null;
            }
            if (creds.getPassword() == null || creds.getPassword().trim().isEmpty()) {
                sendErrorResponse(res, HttpStatus.BAD_REQUEST,
                        "Password is required",
                        "Please check your password.");
                return null;
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    creds.getEmail(), creds.getPassword(), new ArrayList<>());

            try {
                return getAuthenticationManager().authenticate(authenticationToken);
            } catch (DisabledException e) {
                log.warn("User account is not verified: {}", creds.getEmail());
                sendErrorResponse(res, HttpStatus.UNAUTHORIZED,
                        "Account not verified",
                        "Please check your email to verify your account.");
            }

        } catch (JsonParseException e) {
            log.error("Malformed JSON in request body", e);
            throw new InvalidLoginRequestException("Invalid JSON format in login request", HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            log.error("Failed to read user login request model", e);
            throw new InvalidLoginRequestException("Invalid login request data", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Authentication process failed: {}", e.getMessage());
            sendErrorResponse(res, HttpStatus.UNAUTHORIZED,
                    "Authentication processing error",
                    "Please check your credentials (email and password).");
        }
        return null;
    }

    /**
     * Handles unsuccessful authentication attempts.
     * <p>
     * 1. Logs the reason for the failure.
     * 2. Differentiates between failure types (e.g., disabled account, invalid credentials) and sends appropriate error messages.
     * 3. Writes a structured JSON response with error details using the `handleException` method.
     *
     * @param request  the HTTP request that initiated the authentication attempt.
     * @param response the HTTP response to send the failure message.
     * @param failed   the exception representing the reason for authentication failure.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){
        log.error("Authentication failed for request URI: {}, reason: {}", request.getRequestURI(), failed.getMessage());

        try {
            if (failed instanceof DisabledException) {
                handleException(response, new CustomAuthenticationException("Account not verified. Please check your email to verify your account.", HttpStatus.UNAUTHORIZED));
            } else if ("BadCredentialsException".equals(failed.getClass().getSimpleName())) {
                handleException(response, new InvalidLoginRequestException("Invalid credentials provided. Please try again.", HttpStatus.UNAUTHORIZED));
            } else {
                handleException(response, new CustomAuthenticationException("Authentication failed. Please check your credentials.", HttpStatus.UNAUTHORIZED));
            }
        } catch (IOException e) {
            log.error("Error writing the authentication failure response: {}", e.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse res, HttpStatus status, String error, String message) {
        try {
            res.setStatus(status.value());
            res.setContentType("application/json");

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("status", status.value());
            errorDetails.put("error", error);
            errorDetails.put("message", message);
            errorDetails.put("timestamp", LocalDateTime.now());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            res.getWriter().write(mapper.writeValueAsString(errorDetails));

            log.info("Error response sent: {} - {}", status.value(), message);
        } catch (IOException ex) {
            log.error("Failed to send error response: {}", ex.getMessage());
        }
    }

    /**
     * Handles successful authentication attempts.
     * <p>
     * 1. Retrieves user details from the `Authentication` object.
     * 2. Generates access and refresh tokens using utility and service classes.
     * 3. Adds tokens to the response as secure `HttpOnly` cookies.
     * 4. Sets the authentication in the SecurityContext for subsequent requests.
     * 5. Logs the success and any potential errors during token generation or cookie setting.
     *
     * @param req   the HTTP request that initiated the authentication.
     * @param res   the HTTP response for sending cookies.
     * @param chain the filter chain for further processing.
     * @param auth  the authenticated `Authentication` object containing user details.
     * @throws IOException if there is an error writing the response.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException {

        log.info("Successful authentication initiated for request: {}", req.getRequestURI());

        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();

            String accessToken = utils.generateAccessToken(customUserDetails.getUserId(), req);

            refreshTokenService.deleteTokensForUser(customUserDetails.getUserId());

            String refreshToken = refreshTokenService.createRefreshToken(customUserDetails.getUserId(), req);
            log.debug("Generated tokens for user: {}, accessToken (first 8 chars): {}...",
                    customUserDetails.getUserId(),
                    accessToken.substring(0, 8));

            addCookiesToResponse(res, accessToken, refreshToken);

            SecurityContextHolder.getContext().setAuthentication(auth);

            UserMeDTO dto = userService.getCurrentUserInfo();

            res.setStatus(HttpStatus.OK.value());
            res.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.writeValue(res.getWriter(), dto);

            log.info("Successful authentication for user: {}", customUserDetails.getUserId());
        } catch (TokenGenerationException e) {
            log.error("Failed to generate tokens for successful authentication.", e);
            handleException(res, new AuthenticationProcessingException("Error generating tokens after authentication."));
        }
    }

    /**
     * Adds secure HttpOnly cookies for the access and refresh tokens to the response.
     * <p>
     * 1. Configures cookies with the following security settings:
     *    - `HttpOnly`: Prevents JavaScript access to the cookies, enhancing security.
     *    - `Secure`: Ensures cookies are only transmitted over HTTPS.
     *    - `Path`: Defines the scope of each cookie.
     *    - `MaxAge`: Sets the expiration time for each cookie.
     * 2. Adds the cookies to the response.
     * 3. Logs the addition of the cookies for debugging purposes.
     *
     * @param res          the HTTP response to which the cookies are added.
     * @param accessToken  the JWT access token.
     * @param refreshToken the JWT refresh token.
     */
    private void addCookiesToResponse(HttpServletResponse res, String accessToken, String refreshToken) {
        ResponseCookie access = ResponseCookie.from("AccessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(COOKIE_DOMAIN)
                .sameSite("Strict")
                .maxAge(SecurityConstants.ACCESS_TOKEN_EXPIRATION)
                .build();

        ResponseCookie refresh = ResponseCookie.from("RefreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(COOKIE_DOMAIN)
                .sameSite("Strict")
                .maxAge(SecurityConstants.REFRESH_TOKEN_EXPIRATION)
                .build();

        res.addHeader(SET_COOKIE, access.toString());
        res.addHeader(SET_COOKIE, refresh.toString());

        log.debug("Set AccessToken и RefreshToken cookies с SameSite=Strict, Domain=$");
    }

    private void handleException(HttpServletResponse response, CustomException exception) throws IOException {
        response.setStatus(exception.getStatus().value());
        response.setContentType("application/json");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", exception.getStatus().value());
        errorDetails.put("error", exception.getStatus().getReasonPhrase());
        errorDetails.put("message", exception.getErrorMessage());
        errorDetails.put("timestamp", LocalDateTime.now());

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorDetails));

        log.error("Authentication error: {}", exception.getErrorMessage());
    }

}
