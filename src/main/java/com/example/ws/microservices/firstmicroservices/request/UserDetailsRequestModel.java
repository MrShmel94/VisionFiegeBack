package com.example.ws.microservices.firstmicroservices.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class UserDetailsRequestModel {

    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Expertis cannot be empty")
    @Size(min = 5, message = "Expertis must be at least 5 characters long")
    private String expertis;

    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password cannot be empty")
    @ValidPassword
    private String password;
}
