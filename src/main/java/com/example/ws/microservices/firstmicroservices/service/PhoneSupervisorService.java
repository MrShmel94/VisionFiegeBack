package com.example.ws.microservices.firstmicroservices.service;

import com.example.ws.microservices.firstmicroservices.dto.PhoneDTO;

import java.util.List;

public interface PhoneSupervisorService {
    List<PhoneDTO> getAllPhonePerUserId(Integer userId);

    //void setPhoneToUserId(Integer userId);
}
