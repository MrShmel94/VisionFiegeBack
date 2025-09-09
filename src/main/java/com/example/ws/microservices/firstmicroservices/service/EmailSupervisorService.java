package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.emailsupervisor.EmailDTO;

import java.util.List;

public interface EmailSupervisorService {
    List<EmailDTO> getAllEmailsPerUserId(Integer userId);
}
