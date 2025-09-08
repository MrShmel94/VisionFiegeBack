package com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -1095854638775605961L;
    private long id;
    private String userId;
    private String expertis;
    private String email;
    private String password;
    private String brCode;
    private String encryptedPassword;
    private String emailVerificationToken;
    private Boolean emailVerificationStatus = false;
}
