package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.agency;

import com.example.ws.microservices.firstmicroservices.common.cache.RedisPreLoader;

import java.util.List;
import java.util.Optional;

public interface AgencyRedisPreLoader extends RedisPreLoader<AgencyDTO> {
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
