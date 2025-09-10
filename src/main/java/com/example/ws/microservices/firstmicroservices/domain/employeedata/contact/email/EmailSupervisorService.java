package com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.email;

import java.util.List;

public interface EmailSupervisorService {
    List<EmailDTO> getAllEmailsPerUserId(Integer userId);
}
