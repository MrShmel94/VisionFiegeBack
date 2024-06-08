package com.example.ws.microservices.firstmicroservices.controller;

import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.entity.UserEntity;
import com.example.ws.microservices.firstmicroservices.request.UserDetailsRequestModel;
import com.example.ws.microservices.firstmicroservices.response.UserRest;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userServices;

    public UserController(UserService userServices) {
        this.userServices = userServices;
    }

    @GetMapping(path = "/{id}")
    public UserRest getUser(@PathVariable String id){

        UserRest returnUser = new UserRest();

        UserDto getUser = userServices.getUserByUserId(id);
        BeanUtils.copyProperties(getUser, returnUser);

        return returnUser;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails){

        UserRest returnUser = new UserRest();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userServices.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnUser);
        return returnUser;
    }

    @PutMapping(path = "/{id}")
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails){

        UserRest returnUser = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userServices.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, returnUser);

        return returnUser;
    }

    @DeleteMapping
    public String deleteUser(){
        return "";
    }
}
