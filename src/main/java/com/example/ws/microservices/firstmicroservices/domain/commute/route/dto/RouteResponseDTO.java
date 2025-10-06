package app.domain.route.dto;

import app.domain.route.Route;

public record RouteResponseDTO(Long id, String name) {
    public static RouteResponseDTO fromEntity(Route route) {
        return new RouteResponseDTO(route.getId(), route.getName());
    }
}