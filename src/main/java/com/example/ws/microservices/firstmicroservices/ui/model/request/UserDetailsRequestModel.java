package com.example.ws.microservices.firstmicroservices.ui.model.request;

import lombok.*;

@Data
public class UserDetailsRequestModel {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
