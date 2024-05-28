package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);
}
