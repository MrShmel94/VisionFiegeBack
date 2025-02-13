package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import jakarta.annotation.Nullable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void createUser(UserDto user);
    void verifyUser(String userId);
    UserDto updateUser(String id, UserDto userDto);
    void verifyUserAccount(String userId);
    SupervisorAllInformationDTO getSupervisorAllInformation(@Nullable String expertis, @Nullable String UserId);
}
