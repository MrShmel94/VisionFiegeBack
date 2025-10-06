package com.example.ws.microservices.firstmicroservices.domain.commute.route;

import app.common.errorhandling.BusinessException;
import app.common.errorhandling.EntityError;
import app.domain.route.dto.RouteResponseDTO;
import app.domain.route.dto.RouteUpsertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository repo;

    public List<RouteResponseDTO> getAll() {
        return repo.findAll().stream()
                .map(RouteResponseDTO::fromEntity)
                .toList();
    }

    public RouteResponseDTO getById(Long id) {
        var r = repo.findById(id)
                .orElseThrow(() -> new BusinessException(EntityError.ROUTE_NOT_FOUND, id));
        return RouteResponseDTO.fromEntity(r);
    }

    public RouteResponseDTO create(RouteUpsertDTO dto) {
        var r = Route.builder()
                .name(dto.name())
                .build();
        return RouteResponseDTO.fromEntity(repo.save(r));
    }

    public RouteResponseDTO update(Long id, RouteUpsertDTO dto) {
        var r = repo.findById(id)
                .orElseThrow(() -> new BusinessException(EntityError.ROUTE_NOT_FOUND, id));
        r.setName(dto.name());
        return RouteResponseDTO.fromEntity(repo.save(r));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new BusinessException(EntityError.ROUTE_NOT_FOUND, id);
        }
        repo.deleteById(id);
    }
}
