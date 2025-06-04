package com.example.ws.microservices.firstmicroservices.repository.etc.planed;

import com.example.ws.microservices.firstmicroservices.entity.etc.planed.PlanedEmployee;

import java.util.List;

public interface PlanedRepositoryCustom {

    void savePlanedEmployeeTraining(List<PlanedEmployee> planedEmployees);
    void deletePlanedEmployeeTraining(List<PlanedEmployee> planedEmployees, String userId) ;
}
