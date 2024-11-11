package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.customError.CustomException;
import com.example.ws.microservices.firstmicroservices.customError.InvalidIdFormatException;
import com.example.ws.microservices.firstmicroservices.customError.UserAlreadyExistsException;
import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.mapper.UserMapper;
import com.example.ws.microservices.firstmicroservices.repository.UserRepository;
import com.example.ws.microservices.firstmicroservices.request.UserDetailsRequestModel;
import com.example.ws.microservices.firstmicroservices.response.UserRest;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.service.UserLookupService;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * The UserController is a REST controller that handles requests for managing user accounts.
 * It provides endpoints to get a user by ID, create a new user, update an existing user,
 * and verify a user's email.
 */

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserLookupService userLookupService;
    private final UserRepository userRepository;

    @Operation(summary = "Get a user by ID or Email", description = "Returns the user details. For ID, it must contain at least one letter and be at least 30 characters long.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid ID format: must contain at least one letter and be at least 30 characters long"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(path = "/user/{idOrEmail}")
    public ResponseEntity<UserRest> getUser(@PathVariable String idOrEmail) {

        try {
            UserDto userDto;

            if (idOrEmail.contains("@")) {
                userDto = userLookupService.getUserByEmail(idOrEmail);
            } else {
                if (idOrEmail.length() < 30 || !idOrEmail.matches(".*[a-zA-Z]+.*")) {
                    throw new InvalidIdFormatException("Invalid ID: must contain at least one letter and be at least 30 characters long.");
                }
                userDto = userLookupService.getUserByUserId(idOrEmail);
            }

            if (userDto == null) {
                log.warn("User not found with ID or Email: {}", idOrEmail);
                throw new UsernameNotFoundException("User not found.");
            }

            UserRest returnUser = UserMapper.INSTANCE.toUserRest(userDto);

            return ResponseEntity.ok(returnUser);

        } catch (Exception e) {
            log.error("Unexpected error in getUser for ID or Email: {}: ", idOrEmail, e);
            throw new RuntimeException("An unexpected error occurred.");
        }
    }


    @Operation(summary = "Create a new user", description = "Adds a user to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created user",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/sign-up")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDetailsRequestModel userDetails) {
        try {

            UserDto userDto = UserMapper.INSTANCE.toUserDto(userDetails);
            userService.createUser(userDto);

            return ResponseEntity.ok("User created successfully. Please check your email for verification.");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in createUser : ", e);
            throw new RuntimeException("An unexpected error occurred.");
        }
    }

    @PostMapping("/test/{expertis}")
    public ResponseEntity<SupervisorAllInformationDTO> getSupervisorAllInformation(@PathVariable String expertis) {
        return ResponseEntity.ok(userRepository.findByExpertis(expertis).orElse(null));
    }

    @Operation(summary = "Update a user by ID", description = "Updates the details of an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @Valid @RequestBody UserDetailsRequestModel userDetails) {
        try {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userDetails, userDto);

            UserDto updatedUser = userService.updateUser(id, userDto);
            UserRest returnUser = new UserRest();
            BeanUtils.copyProperties(updatedUser, returnUser);

            return ResponseEntity.ok(returnUser);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @Operation(summary = "Verify email", description = "Verify user email using the token sent to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully verified email", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid token or user", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable String token) {
        try {
            if (token.length() != 36) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token format. Token must be exactly 30 characters long.");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication.getPrincipal() instanceof CustomUserDetails currentUser)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
            }

            userService.verifyUser(token, currentUser);

            return ResponseEntity.ok("Email verified successfully.");
        } catch (CustomException ex) {
            return ResponseEntity.status(ex.getStatus()).body(ex.getErrorMessage());
        } catch (Exception e) {
            log.error("Unexpected error during email verification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
