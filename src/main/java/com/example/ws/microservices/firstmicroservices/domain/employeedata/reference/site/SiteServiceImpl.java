package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.site;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.entity.EmployeeMapping;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.agency.Agency;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.country.Country;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department.Department;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.position.Position;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.shift.Shift;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.team.Team;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.SiteRequestModel;
import com.example.ws.microservices.firstmicroservices.common.security.aspects.AccessControl;
import com.example.ws.microservices.firstmicroservices.common.security.aspects.MaskField;
import com.example.ws.microservices.firstmicroservices.common.cache.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteServiceImpl implements SiteService {

    private final RedisService redisService;
    private final SiteRepository siteRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * Retrieves a list of EmployeeDTO objects based on dynamic filters provided in the SiteRequestModel.
     * <p>
     * This method builds a dynamic query using the JPA Criteria API. The filter for the site is mandatory,
     * while filters for department, shift, team, position, agency, and country are applied only if provided.
     * </p>
     *
     * @param siteRequestModel the model containing filter criteria for site and related entities
     * @return a list of EmployeeDTO objects matching the specified filter criteria
     */
    @Override
    @Transactional(readOnly = true)
    @AccessControl(
            minWeight = 35
    )
    @MaskField
    public List<EmployeeDTO> findEmployeesByDynamicFilters(SiteRequestModel siteRequestModel) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmployeeDTO> query = cb.createQuery(EmployeeDTO.class);
        Root<EmployeeMapping> employeeRoot = query.from(EmployeeMapping.class);

        List<Predicate> predicates = new ArrayList<>();

        Join<EmployeeMapping, Site> siteJoin = employeeRoot.join("site", JoinType.INNER);
        predicates.add(cb.equal(siteJoin.get("name"), siteRequestModel.nameSite()));

        Join<EmployeeMapping, Department> departmentJoin = employeeRoot.join("department", JoinType.LEFT);
        if (siteRequestModel.nameDepartment() != null && !siteRequestModel.nameDepartment().isEmpty()) {
            predicates.add(departmentJoin.get("name").in(siteRequestModel.nameDepartment()));
        }

        Join<EmployeeMapping, Shift> shiftJoin = employeeRoot.join("shift", JoinType.LEFT);
        if (siteRequestModel.nameShift() != null && !siteRequestModel.nameShift().isEmpty()) {
            predicates.add(shiftJoin.get("name").in(siteRequestModel.nameShift()));
        }

        Join<EmployeeMapping, Team> teamJoin = employeeRoot.join("team", JoinType.LEFT);
        if (siteRequestModel.nameTeam() != null && !siteRequestModel.nameTeam().isEmpty()) {
            predicates.add(teamJoin.get("name").in(siteRequestModel.nameTeam()));
        }

        Join<EmployeeMapping, Position> positionJoin = employeeRoot.join("position", JoinType.LEFT);
        if (siteRequestModel.namePosition() != null && !siteRequestModel.namePosition().isEmpty()) {
            predicates.add(positionJoin.get("name").in(siteRequestModel.namePosition()));
        }

        Join<EmployeeMapping, Agency> agencyJoin = employeeRoot.join("agency", JoinType.LEFT);
        if (siteRequestModel.nameAgency() != null && !siteRequestModel.nameAgency().isEmpty()) {
            predicates.add(agencyJoin.get("name").in(siteRequestModel.nameAgency()));
        }

        Join<EmployeeMapping, Country> countryJoin = employeeRoot.join("country", JoinType.LEFT);
        if (siteRequestModel.nameCountry() != null && !siteRequestModel.nameCountry().isEmpty()) {
            predicates.add(countryJoin.get("name").in(siteRequestModel.nameCountry()));
        }

        query.select(cb.construct(EmployeeDTO.class,
                        employeeRoot.get("id"),
                        employeeRoot.get("expertis"),
                        employeeRoot.get("zalosId"),
                        employeeRoot.get("brCode"),
                        employeeRoot.get("firstName"),
                        employeeRoot.get("lastName"),
                        employeeRoot.get("isWork"),
                        employeeRoot.get("sex"),
                        siteJoin.get("name"),
                        shiftJoin.get("name"),
                        departmentJoin.get("name"),
                        teamJoin.get("name"),
                        positionJoin.get("name"),
                        agencyJoin.get("name"),
                        employeeRoot.get("isCanHasAccount"),
                        employeeRoot.get("validToAccount")
                ))
                .where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Optional<Site> findSiteByName(String name) {
        return siteRepository.getSiteByName(name);
    }

    @Override
    public List<SiteDTO> getSites() {
        return redisService.getFromCache("sites", new TypeReference<List<SiteDTO>>() {}).orElseGet(() -> {
            List<SiteDTO> allDto = siteRepository.findAll().stream().map(entity -> {
                return SiteDTO.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .build();
            }).toList();
            redisService.saveToCache("sites", allDto);

            return allDto;
        });
    }
}
