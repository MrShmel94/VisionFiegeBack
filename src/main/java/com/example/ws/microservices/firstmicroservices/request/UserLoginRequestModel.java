package com.example.ws.microservices.firstmicroservices.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestModel {

    private String email;
    private String password;
}
