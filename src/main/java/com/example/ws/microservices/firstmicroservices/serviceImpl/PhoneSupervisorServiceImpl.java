package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.PhoneDTO;
import com.example.ws.microservices.firstmicroservices.repository.PhoneRepository;
import com.example.ws.microservices.firstmicroservices.service.PhoneSupervisorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PhoneSupervisorServiceImpl implements PhoneSupervisorService {

    PhoneRepository phoneRepository;

    @Override
    public List<PhoneDTO> getAllPhonePerUserId(Integer userId) {
        return phoneRepository.findAllByPhoneSupervisorId(userId);
    }
}
