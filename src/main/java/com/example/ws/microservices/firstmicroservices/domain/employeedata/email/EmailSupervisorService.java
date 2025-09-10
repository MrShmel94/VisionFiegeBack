package com.example.ws.microservices.firstmicroservices.domain.employeedata.email;

import java.util.List;

public interface EmailSupervisorService {
    List<EmailDTO> getAllEmailsPerUserId(Integer userId);
}
