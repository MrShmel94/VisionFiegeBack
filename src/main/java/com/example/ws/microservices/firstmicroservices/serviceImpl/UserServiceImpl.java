package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.customError.CustomException;
import com.example.ws.microservices.firstmicroservices.customError.UserAlreadyExistsException;
import com.example.ws.microservices.firstmicroservices.customError.VerificationException;
import com.example.ws.microservices.firstmicroservices.dto.*;
import com.example.ws.microservices.firstmicroservices.mapper.UserMapper;
import com.example.ws.microservices.firstmicroservices.repository.UserRoleRepository;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.secure.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.service.*;
import com.example.ws.microservices.firstmicroservices.entity.UserEntity;
import com.example.ws.microservices.firstmicroservices.repository.UserRepository;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmployeeService employeeService;
    private final RoleService roleService;
    private final EmailTokenService emailTokenService;
    private final RedisCacheService redisCacheService;

    @Override
    @Transactional
    public void createUser(UserDto user) {

        userRepository.findByEmailOrExpertis(user.getEmail(), user.getExpertis())
                .ifPresent(existingUser -> {
                    String message = existingUser.getEmail().equals(user.getEmail())
                            ? "User with this email already exists."
                            : "User with this expertis already exists.";
                    throw new UserAlreadyExistsException(message);
                });

        Optional<EmployeeDTO> employee = employeeService.getUsersByExpertisForRegistration(user.getExpertis());

        if(employee.isEmpty()){
            throw new UsernameNotFoundException("No corresponding employee found for the provided expertis.");
        }

        EmployeeDTO employeeEntity = employee.get();

        if(!user.getBrCode().equalsIgnoreCase(employeeEntity.getBrCode())){
            throw new VerificationException("The provided BR-code does not match the employee's BR-code.");
        }

        if(!employeeEntity.getIsCanHasAccount() || employeeEntity.getValidToAccount().isBefore(LocalDateTime.now())){
            throw new VerificationException("The employee is not eligible to create an account, or the account validity period has expired.");
        }

        user.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        generateAndSaveUser(user);

        log.info("Sending email token for user: {}", user.getEmail());
        emailTokenService.processEmailVerification(user.getEmail(), user.getEmailVerificationToken());
    }



    @Override
    @Transactional
    public void verifyUser(String token) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmailVerificationToken(token);

        if (userEntityOptional.isEmpty()) {
            throw new CustomException("Token is invalid or expired.", HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userEntityOptional.get();

        userEntity.setEmailVerificationStatus(true);
        userEntity.setEmailVerificationToken(null);
        userRepository.save(userEntity);

        SupervisorAllInformationDTO allInformation = getSupervisorAllInformation(userEntity.getExpertis(), null);
        allInformation.setEmailVerificationStatus(true);
        redisCacheService.saveToCache("userDetails:" + userEntity.getExpertis(), allInformation);
        redisCacheService.saveMapping(userEntity.getUserId(), allInformation.getExpertis());


        log.info("User with email {} has successfully verified their email.", userEntity.getEmail());
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {

        Optional<UserEntity> getUserById = userRepository.findByUserId(id);

        if(getUserById.isEmpty()){
            throw new UsernameNotFoundException(String.format("User %s not found", id));
        }

        UserEntity updatedUser = userRepository.save(getUserById.get());
        UserDto updatedUserDto = new UserDto();
        BeanUtils.copyProperties(updatedUser, updatedUserDto);

        return updatedUserDto;
    }

    @Override
    @Transactional
    public void verifyUserAccount(String userId) {
        SupervisorAllInformationDTO allInformation = getSupervisorAllInformation(null, userId);
        userRepository.setVerified(allInformation.getUserId());
    }

    @Override
    public SupervisorAllInformationDTO getSupervisorAllInformation(@Nullable String expertis, @Nullable String userId) {

        if (expertis == null && userId == null) {
            throw new IllegalArgumentException("Both expertis and userId cannot be null.");
        }

        String expertisRedis = null;

        if (userId != null) {
            expertisRedis = redisCacheService.getExpertisByUserId(userId);
        }

        String redisKey = expertisRedis != null ? "userDetails:" + expertisRedis : expertis;

        SupervisorAllInformationDTO entity;

        if (redisKey != null) {
            entity = redisCacheService.getFromCache(redisKey, SupervisorAllInformationDTO.class).orElse(null);
        } else {
            entity = null;
        }

        if (entity == null) {
            String attributeToSearch = expertis != null ? expertis : userId;

            entity = userRepository.findByAnyUserAttribute(attributeToSearch)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            String.format("User %s not found", attributeToSearch)));

            String newRedisKey = "userDetails:" + entity.getExpertis();

            redisCacheService.saveToCache("encryptedPassword:" + entity.getExpertis(),
                    entity.getEncryptedPassword());

            entity.setEncryptedPassword(null);

            List<RoleDTO> roles = roleService.getAllRoleByUserId(entity.getId());
            entity.setRoles(roles);

            redisCacheService.saveToCache(newRedisKey, entity);

            if (expertisRedis == null) {
                redisCacheService.saveMapping(entity.getUserId(), entity.getExpertis());
            }
        }

        return entity;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SupervisorAllInformationDTO entity = getSupervisorAllInformation(null, username);


        String encryptedPassword = entity.getEncryptedPassword();
        if (encryptedPassword == null) {
            encryptedPassword = redisCacheService.getFromCache("encryptedPassword:" + entity.getExpertis(), String.class)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            String.format("Password not found for user %s", username)));
        }

        List<RoleDTO> roles = entity.getRoles() != null && !entity.getRoles().isEmpty()
                ? entity.getRoles() : List.of();

        return new CustomUserDetails(
                entity.getUserId(),
                entity.getIsVerified(),
                entity.getEmailVerificationStatus(),
                entity.getIsCanHasAccount(),
                encryptedPassword,
                entity.getValidToAccount(),
                roles
        );
    }

    private void generateAndSaveUser(UserDto user) {
        boolean isSaved = false;
        while (!isSaved) {
            try {
                user.setUserId(UUID.randomUUID().toString().replace("-", "").substring(0, 30));
                userRepository.save(UserMapper.INSTANCE.toUserEntity(user));
                isSaved = true;
            } catch (DataIntegrityViolationException ex) {
                log.warn("UUID collision detected for userId: {}. Retrying...", user.getUserId());
            }
        }
    }
}
