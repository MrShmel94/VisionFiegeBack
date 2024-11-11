package com.example.ws.microservices.firstmicroservices.service;


import com.example.ws.microservices.firstmicroservices.dto.UserDto;

public interface UserLookupService {

    UserDto getUserByUserId(String userId);
    UserDto getUserByEmail(String email);

}
