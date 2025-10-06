package app.domain.employeecard;

import app.common.errorhandling.BusinessException;
import app.common.errorhandling.EntityError;
import app.domain.employeecard.dto.EmployeeCardResponseDTO;
import app.domain.employeecard.dto.EmployeeCardUpsertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeCardService {

    private final EmployeeCardRepository repo;

    public List<EmployeeCardResponseDTO> getAll() {
        return repo.findAll().stream()
                .map(EmployeeCardResponseDTO::fromEntity)
                .toList();
    }

    public EmployeeCardResponseDTO getById(Long id) {
        var e = repo.findById(id).orElseThrow(() ->
                new BusinessException(EntityError.EMPLOYEE_CARD_NOT_FOUND, id));

        return EmployeeCardResponseDTO.fromEntity(e);
    }

    public EmployeeCardResponseDTO create(EmployeeCardUpsertDTO dto) {
        var e = EmployeeCard.builder()
                .cardNumber(dto.cardNumber())
                .build();
        return EmployeeCardResponseDTO.fromEntity(repo.save(e));
    }

    public EmployeeCardResponseDTO update(Long id, EmployeeCardUpsertDTO dto) {
        var e = repo.findById(id).orElseThrow(() ->
                new BusinessException(EntityError.EMPLOYEE_CARD_NOT_FOUND, id));

        e.setCardNumber(dto.cardNumber());
        return EmployeeCardResponseDTO.fromEntity(repo.save(e));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new BusinessException(EntityError.EMPLOYEE_CARD_NOT_FOUND, id);
        }
        repo.deleteById(id);
    }
}
