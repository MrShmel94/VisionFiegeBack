package app.domain.route;

import app.domain.route.dto.RouteResponseDTO;
import app.domain.route.dto.RouteUpsertDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public List<RouteResponseDTO> list() {
        return routeService.getAll();
    }

    @GetMapping("/{id}")
    public RouteResponseDTO get(@PathVariable Long id) {
        return routeService.getById(id);
    }

    @PostMapping
    public RouteResponseDTO create(@RequestBody @Valid RouteUpsertDTO dto) {
        return routeService.create(dto);
    }

    @PutMapping("/{id}")
    public RouteResponseDTO update(@PathVariable Long id, @RequestBody @Valid RouteUpsertDTO dto) {
        return routeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        routeService.delete(id);
    }
}
