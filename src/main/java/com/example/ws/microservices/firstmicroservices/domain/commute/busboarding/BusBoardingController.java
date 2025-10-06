package com.example.ws.microservices.firstmicroservices.domain.commute.busboarding;

import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto.BusBoardingBulkDTO;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto.BusBoardingResponseDTO;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.dto.BusBoardingUpsertDTO;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.service.BusBoardingBulkService;
import com.example.ws.microservices.firstmicroservices.domain.commute.busboarding.service.BusBoardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bus-boarding")
@RequiredArgsConstructor
public class BusBoardingController {

    private final BusBoardingService service;
    private final BusBoardingBulkService bulkService;

    @GetMapping
    public List<BusBoardingResponseDTO> list() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BusBoardingResponseDTO get(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/by-route/{routeId}")
    public List<BusBoardingResponseDTO> byRoute(@PathVariable Long routeId) {
        return service.getByRouteId(routeId);
    }

    @PostMapping
    public BusBoardingResponseDTO create(@RequestBody @Valid BusBoardingUpsertDTO dto) {
        return service.create(dto);
    }

    @PostMapping("/bulk")
    public List<BusBoardingResponseDTO> bulkSave(@RequestBody List<BusBoardingBulkDTO> rows) {
        return bulkService.saveAll(rows);
    }

    @PutMapping("/{id}")
    public BusBoardingResponseDTO update(@PathVariable Long id, @RequestBody @Valid BusBoardingUpsertDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
