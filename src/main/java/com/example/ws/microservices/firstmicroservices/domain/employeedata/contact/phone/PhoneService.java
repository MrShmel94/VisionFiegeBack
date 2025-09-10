package com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.phone;

import java.util.List;

public interface PhoneService {
    List<PhoneDTO> getAllPhonePerUserId(Integer userId);

    //void setPhoneToUserId(Integer userId);
}
