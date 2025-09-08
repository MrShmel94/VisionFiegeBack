package com.example.ws.microservices.firstmicroservices.serviceImpl.controller;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.UserController;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserDto;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserDetailsRequestModel;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.dto.UserRest;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    public void testGetUser_success() throws Exception {
        UserDto userDto = createUserDto("1", "John", "Doe", "john.doe@example.com");
        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(userDto, userRest);

        Mockito.when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser
    public void testGetUser_notFound() throws Exception {
        Mockito.when(userService.getUserByUserId(anyString())).thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));
    }

    @Test
    @WithMockUser
    public void testGetUser_serverError() throws Exception {
        Mockito.when(userService.getUserByUserId(anyString())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred."));
    }

    @Test
    @WithMockUser
    public void testCreateUser_success() throws Exception {
        UserDetailsRequestModel userDetails = createUserDetailsRequestModel("John", "Doe", "john.doe@example.com", "Password123$", "ExpertiseExample");
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(userDto, userRest);

        //Mockito.when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/v1/users/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"Password123$\", \"expertis\": \"ExpertiseExample\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser
    public void testCreateUser_invalidInput() throws Exception {
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"\", \"lastName\": \"\", \"email\": \"invalidEmail\", \"password\": \"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser
    public void testCreateUser_userAlreadyExists() throws Exception {
        UserDetailsRequestModel userDetails = createUserDetailsRequestModel("John", "Doe", "john.doe@example.com", "Password123$", "ExpertiseExample");
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        //Mockito.when(userService.createUser(any(UserDto.class))).thenThrow(new RuntimeException("User already exists"));

        mockMvc.perform(post("/api/v1/users/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"Password123$\" , \"expertis\": \"ExpertiseExample\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already exists or invalid input."));
    }


    private UserDto createUserDto(String userId, String firstName, String lastName, String email) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        return userDto;
    }

    private UserDetailsRequestModel createUserDetailsRequestModel(String firstName, String lastName, String email, String password, String expertis) {
        UserDetailsRequestModel userDetails = new UserDetailsRequestModel();
        userDetails.setFirstName(firstName);
        userDetails.setLastName(lastName);
        userDetails.setEmail(email);
        userDetails.setPassword(password);
        userDetails.setExpertis(expertis);
        return userDetails;
    }
}
