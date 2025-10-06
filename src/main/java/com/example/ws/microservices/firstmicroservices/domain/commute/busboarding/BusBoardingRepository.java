package com.example.ws.microservices.firstmicroservices.domain.commute.busboarding;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import java.util.List;
import java.util.Optional;

public interface BusBoardingRepository extends JpaRepository<BusBoarding, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"employeeCard", "route"}, type = EntityGraph.EntityGraphType.LOAD)
    List<BusBoarding> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"employeeCard", "route"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<BusBoarding> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"employeeCard"}, type = EntityGraph.EntityGraphType.LOAD)
    List<BusBoarding> findByRoute_Id(Long routeId);
}