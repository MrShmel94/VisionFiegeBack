package com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.phone;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PhoneServiceImpl implements PhoneService {

    PhoneRepository phoneRepository;

    @Override
    public List<PhoneDTO> getAllPhonePerUserId(Integer userId) {
        return phoneRepository.findAllByPhoneSupervisorId(userId);
    }
}
