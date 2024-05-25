package com.example.ws.microservices.firstmicroservices.response;

import lombok.Data;

@Data
public class UserRest {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
