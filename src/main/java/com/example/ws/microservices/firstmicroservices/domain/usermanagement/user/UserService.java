package com.example.ws.microservices.firstmicroservices.domain.usermanagement.user;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserMeDTO;
import com.example.ws.microservices.firstmicroservices.dto.*;
import jakarta.annotation.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    void createUser(UserDto user);
    void verifyUser(String userId);
    UserDto updateUser(String id, UserDto userDto);
    void verifyUserAccount(String userId);
    SupervisorAllInformationDTO getSupervisorAllInformation(@Nullable String expertis, @Nullable String UserId);
    Optional<SmallInformationSupervisorDTO> getSmallInformationSupervisor(@Nullable String UserId);

    List<PreviewEmployeeDTO> getAllUsersWithoutVerification();
    List<PreviewEmployeeDTO> getAllUsersVerification();
    UserMeDTO getCurrentUserInfo();

    Optional<String> getUserEncryptedPassword(String userId);
    UserDetails loadUserByUsernameWithoutPassword(String username);
}
