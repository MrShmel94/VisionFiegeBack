package com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service;


import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserDto;

public interface UserLookupService {
    UserDto getUserByUserId(String userId);
    UserDto getUserByEmail(String email);
}
