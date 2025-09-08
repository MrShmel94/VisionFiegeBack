package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.UserEntity;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.UserMapper;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.UserRepository;
import com.example.ws.microservices.firstmicroservices.service.UserLookupService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserLookupServiceImpl implements UserLookupService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<UserEntity> byEmail = userRepository.findByEmail(email);

        if(byEmail.isEmpty()){
            throw new UsernameNotFoundException(String.format("User %s not found", email));
        }

        return UserMapper.INSTANCE.toUserDto(byEmail.get());
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        Optional<UserEntity> getUserById = userRepository.findByUserId(userId);

        if(getUserById.isEmpty()){
            throw new UsernameNotFoundException(String.format("User %s not found", userId));
        }

        return UserMapper.INSTANCE.toUserDto(getUserById.get());
    }
}
