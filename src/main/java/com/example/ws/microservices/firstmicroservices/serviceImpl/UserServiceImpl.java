package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserDto createUser(UserDto user) {
        return null;
    }
}
