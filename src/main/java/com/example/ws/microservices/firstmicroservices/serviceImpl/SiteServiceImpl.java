package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeMapping;
import com.example.ws.microservices.firstmicroservices.entity.template.*;
import com.example.ws.microservices.firstmicroservices.repository.SiteRepository;
import com.example.ws.microservices.firstmicroservices.request.SiteRequestModel;
import com.example.ws.microservices.firstmicroservices.service.SiteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EmployeeDTO> findEmployeesBySiteName(String name) {
        Optional<Site> site = siteRepository.findSiteByName(name);
        return site.map(value -> value.getEmployees().stream().map(employee -> EmployeeDTO.builder()
                .id(employee.getId())
                .expertis(employee.getExpertis())
                .zalosId(employee.getZalosId())
                .brCode(employee.getBrCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .isWork(employee.getIsWork())
                .sex(employee.getSex())
                .siteName(employee.getSite().getName())
                .shiftName(employee.getShift().getName())
                .departmentName(employee.getDepartment().getName())
                .teamName(employee.getTeam().getName())
                .positionName(employee.getPosition().getName())
                .agencyName(employee.getAgency().getName())
                .build()).toList()).orElse(null);
    }

    @Override
    public List<EmployeeDTO> findEmployeesByDepartment(SiteRequestModel siteRequestModel) {
        System.out.println(siteRequestModel.toString());
        return List.of();
//        Optional<Site> site = siteRepository.findSiteByName(siteRequestModel.nameSite());
//        List<EmployeeDTO> list = site.get().getDepartments().stream().filter(name -> name.getName().equals(siteRequestModel.nameDepartment())).toList().getFirst().getAllEmployee().stream().map(employee -> EmployeeDTO.builder()
//                .id(employee.getId())
//                .expertis(employee.getExpertis())
//                .zalosId(employee.getZalosId())
//                .brCode(employee.getBrCode())
//                .firstName(employee.getFirstName())
//                .lastName(employee.getLastName())
//                .isWork(employee.getIsWork())
//                .sex(employee.getSex())
//                .siteName(employee.getSite().getName())
//                .shiftName(employee.getShift().getName())
//                .departmentName(employee.getDepartment().getName())
//                .teamName(employee.getTeam().getName())
//                .positionName(employee.getPosition().getName())
//                .agencyName(employee.getAgency().getName())
//                .build()).toList();
//        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> findEmployeesByDynamicFilters(SiteRequestModel siteRequestModel) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmployeeDTO> query = cb.createQuery(EmployeeDTO.class);
        Root<EmployeeMapping> employee = query.from(EmployeeMapping.class);

        List<Predicate> predicates = new ArrayList<>();

        Join<EmployeeMapping, Site> siteJoin = employee.join("site", JoinType.INNER);
        predicates.add(cb.equal(siteJoin.get("name"), siteRequestModel.nameSite()));

        if (siteRequestModel.nameDepartment() != null) {
            Join<EmployeeMapping, Department> departmentJoin = employee.join("department", JoinType.LEFT);
            predicates.add(cb.equal(departmentJoin.get("name"), siteRequestModel.nameDepartment()));
        }
        if (siteRequestModel.nameShift() != null) {
            Join<EmployeeMapping, Shift> shiftJoin = employee.join("shift", JoinType.LEFT);
            predicates.add(cb.equal(shiftJoin.get("name"), siteRequestModel.nameShift()));
        }
        if (siteRequestModel.nameTeam() != null) {
            Join<EmployeeMapping, Team> teamJoin = employee.join("team", JoinType.LEFT);
            predicates.add(cb.equal(teamJoin.get("name"), siteRequestModel.nameTeam()));
        }
        if (siteRequestModel.namePosition() != null) {
            Join<EmployeeMapping, Position> positionJoin = employee.join("position", JoinType.LEFT);
            predicates.add(cb.equal(positionJoin.get("name"), siteRequestModel.namePosition()));
        }
        if (siteRequestModel.nameAgency() != null) {
            Join<EmployeeMapping, Agency> agencyJoin = employee.join("agency", JoinType.LEFT);
            predicates.add(cb.equal(agencyJoin.get("name"), siteRequestModel.nameAgency()));
        }
        if (siteRequestModel.nameCountry() != null) {
            Join<EmployeeMapping, Country> countryJoin = employee.join("county", JoinType.LEFT);
            predicates.add(cb.equal(countryJoin.get("name"), siteRequestModel.nameCountry()));
        }

        query.select(cb.construct(EmployeeDTO.class,
                employee.get("id"),
                employee.get("expertis"),
                employee.get("zalosId"),
                employee.get("brCode"),
                employee.get("firstName"),
                employee.get("lastName"),
                employee.get("isWork"),
                employee.get("sex"),
                siteJoin.get("name"),
                employee.join("shift", JoinType.LEFT).get("name"),
                employee.join("department", JoinType.LEFT).get("name"),
                employee.join("team", JoinType.LEFT).get("name"),
                employee.join("position", JoinType.LEFT).get("name"),
                employee.join("agency", JoinType.LEFT).get("name")
        )).where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }
}
