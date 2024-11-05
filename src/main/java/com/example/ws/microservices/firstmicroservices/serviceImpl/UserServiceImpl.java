package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.customError.UserAlreadyExistsException;
import com.example.ws.microservices.firstmicroservices.customError.VerificationException;
import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.entity.Employee;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeRepository;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.service.EmployeeService;
import com.example.ws.microservices.firstmicroservices.service.UserRoleService;
import com.example.ws.microservices.firstmicroservices.utils.EmailService;
import com.example.ws.microservices.firstmicroservices.utils.Utils;
import com.example.ws.microservices.firstmicroservices.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.entity.UserEntity;
import com.example.ws.microservices.firstmicroservices.repository.UserRepository;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmployeeService employeeService;
    private final UserRoleService userRoleService;
    private final EmailService emailService;

    @Override
    @Transactional
    public UserDto createUser(UserDto user) {

        Optional<UserEntity> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        Optional<UserEntity> existingUserByExpertis = userRepository.findByExpertis(user.getExpertis());


        if (existingUserByEmail.isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists.");
        }

        if (existingUserByExpertis.isPresent()) {
            throw new UserAlreadyExistsException("User with this expertis already exists.");
        }

        user.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setUserId(generateUniqueUserId(30));

        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        emailService.sendVerificationEmail(userEntity);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, userDto);

        return userDto;
    }

    @Override
    public void verifyUser(String userIdToVerify) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        String currentUserId = currentUser.getUserId();

        if (currentUserId.equals(userIdToVerify)) {
            throw new VerificationException("The user cannot verify themselves.");
        }

        Optional<UserEntity> userToVerify = userRepository.findByUserId(userIdToVerify);
        if (userToVerify.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User %s not found", userIdToVerify));
        }

        UserEntity userEntity = userToVerify.get();
        userEntity.setIsVerified(true);
        userRepository.save(userEntity);
    }

    @Override
    public UserDto getUser(String email) {
        Optional<UserEntity> byEmail = userRepository.findByEmail(email);
        if(byEmail.isEmpty()){
            throw new UsernameNotFoundException(String.format("User %s not found", email));
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(byEmail, userDto);

        return userDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        Optional<UserEntity> getUserById = userRepository.findByUserId(userId);

        if(getUserById.isEmpty()){
            throw new UsernameNotFoundException(String.format("User %s not found", userId));
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(getUserById, userDto);

        return userDto;
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {

        Optional<UserEntity> getUserById = userRepository.findByUserId(id);

        if(getUserById.isEmpty()){
            throw new UsernameNotFoundException(String.format("User %s not found", id));
        }

        getUserById.get().setFirstName(userDto.getFirstName());
        getUserById.get().setLastName(userDto.getLastName());


        UserEntity updatedUser = userRepository.save(getUserById.get());
        UserDto updatedUserDto = new UserDto();
        BeanUtils.copyProperties(updatedUser, updatedUserDto);

        return updatedUserDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> byEmail = userRepository.findByEmail(username);
        if(byEmail.isEmpty()){
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        UserEntity userEntity = byEmail.get();

        Optional<EmployeeDTO> employee = employeeService.getUsersByExpertis(userEntity.getExpertis());

        if(employee.isEmpty()){
            throw new UsernameNotFoundException(String.format("Employee not found for expertis: %s", userEntity.getExpertis()));
        }
        EmployeeDTO employeeEntity = employee.get();

        List<GrantedAuthority> authorities = userRoleService.findRolesByUserId(employeeEntity.getId())
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new CustomUserDetails(
                userEntity.getUserId(),
                employeeEntity.getDepartmentName(),
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                userEntity.getIsVerified(),
                employeeEntity.getFirstName(),
                employeeEntity.getLastName(),
                employeeEntity.getSiteName(),
                authorities,
                employeeEntity.getPositionName()
                );
    }

    private String generateUniqueUserId(int length) {
        String userId;
        do {
            userId = utils.generateUserId(length);
        } while (userRepository.findByUserId(userId).isPresent());
        return userId;
    }
}
