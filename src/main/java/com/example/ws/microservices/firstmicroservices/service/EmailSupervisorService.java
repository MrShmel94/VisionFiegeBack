package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.EmailDTO;
import com.example.ws.microservices.firstmicroservices.dto.PhoneDTO;

import java.util.List;

public interface EmailSupervisorService {
    List<EmailDTO> getAllEmailsPerUserId(Integer userId);
}
