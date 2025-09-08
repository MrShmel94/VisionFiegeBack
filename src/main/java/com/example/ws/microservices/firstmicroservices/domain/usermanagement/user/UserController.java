package com.example.ws.microservices.firstmicroservices.domain.usermanagement.user;

import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserMeDTO;
import com.example.ws.microservices.firstmicroservices.request.AssignRoleUserRequest;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserDetailsRequestModel;
import com.example.ws.microservices.firstmicroservices.response.ResponseUsersNotVerification;
import com.example.ws.microservices.firstmicroservices.service.AccessManagementService;
import com.example.ws.microservices.firstmicroservices.service.UserLookupService;
import com.example.ws.microservices.firstmicroservices.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final UserRoleService userRoleService;
    private final AccessManagementService accessManagementService;

//    @Operation(summary = "Get a user by ID or Email", description = "Returns the user details. For ID, it must contain at least one letter and be at least 30 characters long.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
//                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserRest.class)) }),
//            @ApiResponse(responseCode = "400", description = "Invalid ID format: must contain at least one letter and be at least 30 characters long"),
//            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
//            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
//    })
//    @GetMapping(path = "/user/{idOrEmail}")
//    public ResponseEntity<UserRest> getUser(@PathVariable String idOrEmail) {
//
//        try {
//            UserDto userDto;
//
//            if (idOrEmail.contains("@")) {
//                userDto = userLookupService.getUserByEmail(idOrEmail);
//            } else {
//                if (idOrEmail.length() < 30 || !idOrEmail.matches(".*[a-zA-Z]+.*")) {
//                    throw new InvalidIdFormatException("Invalid ID: must contain at least one letter and be at least 30 characters long.");
//                }
//                userDto = userLookupService.getUserByUserId(idOrEmail);
//            }
//
//            if (userDto == null) {
//                log.warn("User not found with ID or Email: {}", idOrEmail);
//                throw new UsernameNotFoundException("User not found.");
//            }
//
//            UserRest returnUser = UserMapper.INSTANCE.toUserRest(userDto);
//
//            return ResponseEntity.ok(returnUser);
//
//        } catch (Exception e) {
//            log.error("Unexpected error in getUser for ID or Email: {}: ", idOrEmail, e);
//            throw new RuntimeException("An unexpected error occurred.");
//        }
//    }


    @Operation(summary = "Create a new user", description = "Adds a user to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created user",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/sign-up")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserDetailsRequestModel userDetails) {
        UserDto userDto = UserMapper.INSTANCE.toUserDto(userDetails);
        userService.createUser(userDto);
        return ResponseEntity.ok("User created successfully. Please check your email for verification.");
    }

    @PostMapping("/user/{expertis}")
    public ResponseEntity<SupervisorAllInformationDTO> getSupervisorAllInformation(@PathVariable String expertis) {
        return ResponseEntity.ok(userService.getSupervisorAllInformation(expertis, null));
    }

    @Operation(summary = "Verify email", description = "Verify user email using the token sent to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully verified email", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid token or user", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/verify-email/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable String token) {

        if (token.length() != 36) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token format. Token must be exactly 36 characters long.");
        }

        userService.verifyUser(token);
        return ResponseEntity.ok("Email verified successfully.");
    }

    @Operation(summary = "Verify account employee", description = "Verify user account via verification users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully verified account", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid userId", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/verify-account")
    public ResponseEntity<?> verifyUserAccount(@Valid @RequestBody AssignRoleUserRequest requestModel) {

        accessManagementService.verifyAccountAndAssignRole(requestModel);
        return ResponseEntity.ok("Account verified successfully.");
    }

    @Operation(summary = "Assign role to a user",
            description = "Assigns a specific role to a user by their ID. The role must exist in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or role not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRoleToUser(@Valid @RequestBody AssignRoleUserRequest requestModel) {

            userRoleService.assignRoleToUser(requestModel);
            return ResponseEntity.ok("Role assigned successfully.");
    }


    @GetMapping("/remove-role")
    public ResponseEntity<?> deleteRoleToUser(@RequestParam String expertis, @RequestParam String roleName) {
        userRoleService.removeRoleFromUser(expertis, roleName);
        return ResponseEntity.ok("Role deleted successfully.");
    }

    @GetMapping("/not-verified")
    public ResponseEntity<ResponseUsersNotVerification> getAllUsersNotVerified(){
        return ResponseEntity.status(HttpStatus.OK).body(accessManagementService.getAllUsersNotVerified());
    }

    @GetMapping("/verified")
    public ResponseEntity<ResponseUsersNotVerification> getAllUsersVerified(){
        return ResponseEntity.status(HttpStatus.OK).body(accessManagementService.getAllUsersAccount());
    }

    @Operation(summary = "Get current user info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    @GetMapping("/me")
    public ResponseEntity<UserMeDTO> getMe() {
        UserMeDTO dto = userService.getCurrentUserInfo();
        return ResponseEntity.ok(dto);
    }
}
