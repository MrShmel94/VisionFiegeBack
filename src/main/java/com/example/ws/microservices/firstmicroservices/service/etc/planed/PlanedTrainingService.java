package com.example.ws.microservices.firstmicroservices.service.etc.planed;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.etc.planed.PlanedTrainingDTO;
import com.example.ws.microservices.firstmicroservices.request.etc.PlaningTrainingRequest;

import java.time.LocalDate;
import java.util.List;

public interface PlanedTrainingService {

    List<PlanedTrainingDTO> getAllPlanedTrainingsBetweenDate(LocalDate startDate, LocalDate endDate);
    List<PlanedTrainingDTO> getAllPlanedTrainingsByDocumentName(String documentName);
    void savePlanedTraining(PlaningTrainingRequest requestModel);
    void updatePlanedTraining(PlanedTrainingDTO planedTrainingDTO);
    void deletePlanedTraining(Long id);
    PlanedTrainingDTO getPlanedTrainingsById(Long id);
    public List<PreviewEmployeeDTO> getAllAvailableEmployeeByTrainingId(Long id);
}
