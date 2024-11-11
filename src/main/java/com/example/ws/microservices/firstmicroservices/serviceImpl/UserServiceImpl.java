package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.customError.CustomException;
import com.example.ws.microservices.firstmicroservices.customError.UserAlreadyExistsException;
import com.example.ws.microservices.firstmicroservices.customError.VerificationException;
import com.example.ws.microservices.firstmicroservices.dto.*;
import com.example.ws.microservices.firstmicroservices.mapper.UserMapper;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.service.*;
import com.example.ws.microservices.firstmicroservices.utils.Utils;
import com.example.ws.microservices.firstmicroservices.entity.UserEntity;
import com.example.ws.microservices.firstmicroservices.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmployeeService employeeService;
    private final UserRoleService userRoleService;
    private final PhoneSupervisorService phoneSupervisorService;
    private final EmailSupervisorService emailSupervisorService;
    private final EmailTokenService emailTokenService;

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

        user.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        generateAndSaveUser(user);

        log.info("Sending email token for user: {}", user.getEmail());
        emailTokenService.processEmailVerification(user.getEmail());
    }



    @Override
    public void verifyUser(String token, CustomUserDetails currentUser) {

        Optional<UserEntity> userEntityOptional = userRepository.findByEmailVerificationToken(token);

        if (userEntityOptional.isEmpty()) {
            throw new CustomException("Token is invalid or expired.", HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userEntityOptional.get();

        if (!currentUser.getUsername().equals(userEntity.getEmail())) {
            throw new CustomException("Token does not belong to the authenticated user.", HttpStatus.FORBIDDEN);
        }

        userEntity.setEmailVerificationStatus(true);
        userRepository.save(userEntity);

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
    public Optional<SupervisorAllInformationDTO> getSupervisorAllInformation(String userId) {
        Optional<SupervisorAllInformationDTO> byExpertis = userRepository.findByExpertis(userId);
        if(byExpertis.isEmpty()){
            throw new UsernameNotFoundException(String.format("User %s not found", userId));
        }

        List<EmailDTO> emails = emailSupervisorService.getAllEmailsPerUserId(Integer.parseInt(userId));
        List<PhoneDTO> phones = phoneSupervisorService.getAllPhonePerUserId(Integer.parseInt(userId));
        List<RoleDTO> roles = userRoleService.getAllRolePerUserId(Long.valueOf(userId));

        return null;
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

//        List<GrantedAuthority> authorities = userRoleService.findRolesByUserId(employeeEntity.getId())
//                .stream()
//                .map(role -> new SimpleGrantedAuthority(role.getName()))
//                .collect(Collectors.toList());

        //TODO
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomUserDetails(
                userEntity.getUserId(),
                employeeEntity.getDepartmentName(),
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                userEntity.getIsVerified(),
                userEntity.getEmailVerificationStatus(),
                employeeEntity.getFirstName(),
                employeeEntity.getLastName(),
                employeeEntity.getSiteName(),
                authorities,
                employeeEntity.getPositionName()
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
