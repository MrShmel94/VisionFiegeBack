package com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.contacttype;

import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactTypeServiceImpl implements ContactTypeService {

    private final ContactTypeRepository repository;
    private final ContactTypeServiceCache contactTypeServiceCache;
    private final RedisCacheService redisCacheService;

}
