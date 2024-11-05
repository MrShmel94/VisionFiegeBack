package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.customError.CustomException;
import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.request.UserDetailsRequestModel;
import com.example.ws.microservices.firstmicroservices.response.UserRest;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get a user by ID", description = "Returns the user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            UserDto userDto = userService.getUserByUserId(id);
            UserRest returnUser = new UserRest();
            BeanUtils.copyProperties(userDto, returnUser);

            return ResponseEntity.ok(returnUser);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @Operation(summary = "Create a new user", description = "Adds a user to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created user",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/sign-up")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDetailsRequestModel userDetails) {
        try {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userDetails, userDto);

            UserDto createdUser = userService.createUser(userDto);
            UserRest returnUser = new UserRest();
            BeanUtils.copyProperties(createdUser, returnUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(returnUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists or invalid input.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
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
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("code") String code) {
        try {
            userService.verifyUser(code);
            return ResponseEntity.ok("Email verified successfully.");
        } catch (CustomException ex) {
            return ResponseEntity.status(ex.getStatus()).body(ex.getErrorMessage());
        }
    }


}
