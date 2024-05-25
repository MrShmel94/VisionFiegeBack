package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.response.UserRest;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import com.example.ws.microservices.firstmicroservices.ui.model.request.UserDetailsRequestModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userServices;

    @GetMapping
    public String getUser(){
        return "";
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails){

        UserRest returnUser = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userServices.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnUser);
        return returnUser;
    }

    @PutMapping
    public String updateUser(){
        return "";
    }

    @DeleteMapping
    public String deleteUser(){
        return "";
    }
}
