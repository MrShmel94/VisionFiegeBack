package com.example.ws.microservices.firstmicroservices.service.cache;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.AgencyDTO;
import com.example.ws.microservices.firstmicroservices.entity.vision.simpleTables.Agency;
import com.example.ws.microservices.firstmicroservices.service.redice.CachingService;

import java.util.List;
import java.util.Optional;

public interface AgencyServiceCache extends CachingService<AgencyDTO> {
    List<Agency> findAll();
    List<AgencyDTO> getAllFromDB();
    Optional<Agency> findById(Long id);
    Optional<Agency> findByAgencyName(String agencyName);
    Optional<Agency> findByAgencyCode(String agencyCode);
    Optional<Agency> findByAgencyNameAndAgencyCode(String agencyName, String agencyCode);
    Optional<Agency> save(Agency agency);
    Optional<Agency> update(Agency agency);
    Optional<Agency> delete(Long id);
}
