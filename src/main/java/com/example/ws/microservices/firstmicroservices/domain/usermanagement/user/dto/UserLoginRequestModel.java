package com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestModel{

    private String email;
    private String password;

}
