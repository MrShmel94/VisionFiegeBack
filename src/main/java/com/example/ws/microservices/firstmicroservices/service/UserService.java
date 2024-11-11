package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.entity.UserEntity;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    void createUser(UserDto user);
    void verifyUser(String userId, CustomUserDetails currentUser);
    UserDto updateUser(String id, UserDto userDto);

    Optional<SupervisorAllInformationDTO> getSupervisorAllInformation(String userId);
}
