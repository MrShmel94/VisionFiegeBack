package com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.service;

import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.BusBoarding;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.BusBoardingRepository;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto.BusBoardingBulkDTO;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto.BusBoardingResponseDTO;
import com.example.ws.microservices.firstmicroservices.domain.commute.employeecard.EmployeeCard;
import com.example.ws.microservices.firstmicroservices.domain.commute.employeecard.EmployeeCardRepository;
import com.example.ws.microservices.firstmicroservices.domain.commute.route.Route;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BusBoardingBulkService {

    private final EmployeeCardRepository employeeCardRepo;
    private final BusBoardingRepository busBoardingRepo;
    private final EntityManager em;

    public List<BusBoardingResponseDTO> saveAll(List<BusBoardingBulkDTO> rows) {
        if (rows == null || rows.isEmpty()) return List.of();

        Set<String> cardNumbers = rows.stream()
                .map(BusBoardingBulkDTO::employeeCardNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, EmployeeCard> existing = employeeCardRepo.findByCardNumberIn(cardNumbers)
                .stream()
                .collect(Collectors.toMap(EmployeeCard::getCardNumber, Function.identity()));

        List<EmployeeCard> missing = cardNumbers.stream()
                .filter(n -> !existing.containsKey(n))
                .map(n -> EmployeeCard.builder().cardNumber(n).build())
                .toList();

        if (!missing.isEmpty()) {
            employeeCardRepo.saveAll(missing).forEach(ec -> existing.put(ec.getCardNumber(), ec));
        }

        Map<Long, Route> routeRefs = rows.stream()
                .map(BusBoardingBulkDTO::routeId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(id -> id, id -> em.getReference(Route.class, id)));

        List<BusBoarding> boardings = rows.stream()
                .map(r -> BusBoarding.builder()
                        .boardedAt(r.boardedAt())
                        .employeeCard(existing.get(r.employeeCardNumber()))
                        .route(routeRefs.get(r.routeId()))
                        .build())
                .toList();

        List<BusBoarding> saved = busBoardingRepo.saveAll(boardings);
        return saved.stream()
                .map(BusBoardingResponseDTO::fromEntity)
                .toList();
    }
}
