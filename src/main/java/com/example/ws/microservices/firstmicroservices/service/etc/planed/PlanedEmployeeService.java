package com.example.ws.microservices.firstmicroservices.service.etc.planed;

import com.example.ws.microservices.firstmicroservices.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.etc.planed.PlanedEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.request.etc.MultiPlanedEmployeeRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface PlanedEmployeeService {

    List<PlanedEmployeeDTO> getAllEmployeesByTrainingIds(List<Long> trainingIds);
    void savePlanedEmployeeTraining(MultiPlanedEmployeeRequest request);
    void deletePlanedEmployeeTraining(MultiPlanedEmployeeRequest request);
    void setPlanedEmployeeTraining(MultiPlanedEmployeeRequest request);
    List<PreviewEmployeeDTO> getAllEmployeesAvailableForTraining(LocalDate dateTraining, LocalTime startTime, LocalTime endTime, Set<String> positionName, String nameDoc);
}
