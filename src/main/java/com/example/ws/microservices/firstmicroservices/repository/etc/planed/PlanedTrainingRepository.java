package com.example.ws.microservices.firstmicroservices.repository.etc.planed;

import com.example.ws.microservices.firstmicroservices.dto.etc.planed.PlanedTrainingDTO;
import com.example.ws.microservices.firstmicroservices.entity.etc.planed.PlanedTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanedTrainingRepository extends JpaRepository<PlanedTraining, Long> {

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.etc.planed.PlanedTrainingDTO(
           pt.id, pt.date, pt.timeStart, pt.timeFinish, pt.maxCountEmployee, doc.name, pt.positions, pt.nameTrainers, pt.place, pt.description, CONCAT(emp.firstName, ' ' , emp.lastName)
           ) FROM PlanedTraining pt
           JOIN Document doc ON pt.document.id = doc.id
           JOIN UserEntity user ON pt.userId = user.userId
           JOIN Employee emp ON user.expertis= emp.expertis
           WHERE pt.date BETWEEN :start AND :finish
           """)
    List<PlanedTrainingDTO> getAllTrainingsBetweenDate(@Param("start") LocalDate dateStart, @Param("finish") LocalDate dateEnd);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.etc.planed.PlanedTrainingDTO(
           pt.id, pt.date, pt.timeStart, pt.timeFinish, pt.maxCountEmployee, doc.name, pt.positions, pt.nameTrainers, pt.place, pt.description, CONCAT(emp.firstName, ' ' , emp.lastName)
           ) FROM PlanedTraining pt
           JOIN Document doc ON pt.document.id = doc.id
           JOIN UserEntity user ON pt.userId = user.userId
           JOIN Employee emp ON user.expertis= emp.expertis
           WHERE doc.name = :documentName
           """)
    List<PlanedTrainingDTO> getAllTrainingsByDocumentName(@Param("documentName") String documentName);

    @Query("""
           SELECT new com.example.ws.microservices.firstmicroservices.dto.etc.planed.PlanedTrainingDTO(
           pt.id, pt.date, pt.timeStart, pt.timeFinish, pt.maxCountEmployee, doc.name, pt.positions, pt.nameTrainers, pt.place, pt.description, CONCAT(emp.firstName, ' ' , emp.lastName)
           ) FROM PlanedTraining pt
           JOIN Document doc ON pt.document.id = doc.id
           JOIN UserEntity user ON pt.userId = user.userId
           JOIN Employee emp ON user.expertis= emp.expertis
           WHERE pt.id = :id
           """)
    Optional<PlanedTrainingDTO> getPlanedTrainingById(@Param("id") Long id);
}
