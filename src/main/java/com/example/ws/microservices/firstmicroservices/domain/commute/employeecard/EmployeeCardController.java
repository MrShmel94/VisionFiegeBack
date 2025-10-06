package com.example.ws.microservices.firstmicroservices.domain.commute.employeecard;

import app.domain.employeecard.dto.EmployeeCardResponseDTO;
import app.domain.employeecard.dto.EmployeeCardUpsertDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee-card")
@RequiredArgsConstructor
public class EmployeeCardController {

    private final EmployeeCardService service;

    @GetMapping
    public List<EmployeeCardResponseDTO> list() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EmployeeCardResponseDTO get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public EmployeeCardResponseDTO create(@RequestBody @Valid EmployeeCardUpsertDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public EmployeeCardResponseDTO update(@PathVariable Long id, @RequestBody @Valid EmployeeCardUpsertDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}