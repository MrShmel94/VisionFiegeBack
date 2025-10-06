package com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.service;

import com.example.ws.microservices.firstmicroservices.common.errorhandling.BusinessException;
import com.example.ws.microservices.firstmicroservices.common.errorhandling.EntityError;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.BusBoarding;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.BusBoardingRepository;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto.BusBoardingResponseDTO;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto.BusBoardingUpsertDTO;
import com.example.ws.microservices.firstmicroservices.domain.commute.employeecard.EmployeeCardRepository;
import com.example.ws.microservices.firstmicroservices.domain.commute.route.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusBoardingService {

    private final BusBoardingRepository repo;
    private final EmployeeCardRepository employeeCardRepo;
    private final RouteRepository routeRepo;

    public List<BusBoardingResponseDTO> getAll() {
        return repo.findAll().stream()
                .map(BusBoardingResponseDTO::fromEntity)
                .toList();
    }

    public BusBoardingResponseDTO getById(Long id) {
        var b = repo.findById(id).orElseThrow(() ->
                new BusinessException(EntityError.BUS_BOARDING_NOT_FOUND, id));

        return BusBoardingResponseDTO.fromEntity(b);
    }

    public List<BusBoardingResponseDTO> getByRouteId(Long routeId) {
        return repo.findByRoute_Id(routeId).stream()
                .map(BusBoardingResponseDTO::fromEntity)
                .toList();
    }

    public BusBoardingResponseDTO create(BusBoardingUpsertDTO dto) {
        var card = employeeCardRepo.findById(dto.employeeCardId())
                .orElseThrow(() ->
                        new BusinessException(EntityError.EMPLOYEE_CARD_NOT_FOUND, dto.employeeCardId()));

        var route = routeRepo.findById(dto.routeId())
                .orElseThrow(() ->
                        new BusinessException(EntityError.ROUTE_NOT_FOUND, dto.routeId()));

        var entity = BusBoarding.builder()
                .boardedAt(dto.boardedAt())
                .employeeCard(card)
                .route(route)
                .build();

        return BusBoardingResponseDTO.fromEntity(repo.save(entity));
    }

    public BusBoardingResponseDTO update(Long id, BusBoardingUpsertDTO dto) {
        var entity = repo.findById(id).orElseThrow(() ->
                new BusinessException(EntityError.BUS_BOARDING_NOT_FOUND, id));

        var card = employeeCardRepo.findById(dto.employeeCardId())
                .orElseThrow(() ->
                        new BusinessException(EntityError.EMPLOYEE_CARD_NOT_FOUND, dto.employeeCardId()));

        var route = routeRepo.findById(dto.routeId())
                .orElseThrow(() -> new BusinessException(EntityError.ROUTE_NOT_FOUND, dto.routeId()));

        entity.setBoardedAt(dto.boardedAt());
        entity.setEmployeeCard(card);
        entity.setRoute(route);

        return BusBoardingResponseDTO.fromEntity(repo.save(entity));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new BusinessException(EntityError.BUS_BOARDING_NOT_FOUND, id);
        repo.deleteById(id);
    }
}
