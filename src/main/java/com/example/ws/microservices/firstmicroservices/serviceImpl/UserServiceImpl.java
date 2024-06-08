package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.Utils;
import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.entity.UserEntity;
import com.example.ws.microservices.firstmicroservices.repository.UserRepository;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, Utils utils, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto user) {

        UserEntity storedAlreadyUserDetails = userRepository.findByEmail(user.getEmail());

        if(storedAlreadyUserDetails != null){
            throw new RuntimeException("User already exists");
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);


        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        userEntity.setUserId(utils.generateUserId(30));

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, userDto);

        return userDto;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity byEmail = userRepository.findByEmail(email);
        if(byEmail == null){
            throw new UsernameNotFoundException(String.format("User %s not found", email));
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(byEmail, userDto);

        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity getUserById = userRepository.findByUserId(userId);
        if(getUserById == null){
            throw new UsernameNotFoundException(String.format("User %s not found", userId));
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(getUserById, userDto);

        return userDto;
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {

        UserEntity getUserById = userRepository.findByUserId(id);

        if(getUserById == null){
            throw new UsernameNotFoundException(String.format("User %s not found", id));
        }

        getUserById.setFirstName(userDto.getFirstName());
        getUserById.setLastName(userDto.getLastName());


        UserEntity updatedUser = userRepository.save(getUserById);
        UserDto updatedUserDto = new UserDto();
        BeanUtils.copyProperties(updatedUser, updatedUserDto);

        return updatedUserDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity byEmail = userRepository.findByEmail(username);
        if(byEmail == null){
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        return new User(byEmail.getEmail(), byEmail.getEncryptedPassword(), new ArrayList<>());
    }
}
