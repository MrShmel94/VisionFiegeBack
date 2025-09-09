package com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto;

import com.example.ws.microservices.firstmicroservices.oldstructure.request.ValidBrCode;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class UserDetailsRequestModel {

    @NotBlank(message = "Expertis cannot be empty")
    @Size(min = 5, message = "Expertis must be at least 5 characters long")
    private String expertis;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank(message = "Password cannot be empty")
    @ValidPassword
    private String password;

    @NotBlank(message = "BR-code cannot be empty")
    @ValidBrCode
    private String brCode;
}
