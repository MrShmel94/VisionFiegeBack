package app.domain.employeecard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EmployeeCardRepository extends JpaRepository<EmployeeCard, Long> {
    List<EmployeeCard> findByCardNumberIn(Collection<String> cardNumbers);
}
