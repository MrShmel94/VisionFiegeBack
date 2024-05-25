package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import org.springframework.stereotype.Service;

public interface UserService {
    UserDto createUser(UserDto user);
}
