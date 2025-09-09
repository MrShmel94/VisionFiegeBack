package com.example.ws.microservices.firstmicroservices.serviceImpl.etc.planed;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employee.dto.PreviewEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.etc.DocumentDTO;
import com.example.ws.microservices.firstmicroservices.dto.etc.planed.PlanedEmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.etc.planed.PlanedTrainingDTO;
import com.example.ws.microservices.firstmicroservices.entity.etc.Document;
import com.example.ws.microservices.firstmicroservices.entity.etc.planed.PlanedTraining;
import com.example.ws.microservices.firstmicroservices.repository.etc.planed.PlanedTrainingRepository;
import com.example.ws.microservices.firstmicroservices.request.etc.PlaningTrainingRequest;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.secure.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.service.etc.DocumentsService;
import com.example.ws.microservices.firstmicroservices.service.etc.planed.PlanedEmployeeService;
import com.example.ws.microservices.firstmicroservices.service.etc.planed.PlanedTrainingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanedTrainingServiceImpl implements PlanedTrainingService {

    private final DocumentsService documentsService;
    private final PlanedTrainingRepository planedTrainingRepository;
    private final PlanedEmployeeService planedEmployeeService;


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PlanedTrainingDTO> getAllPlanedTrainingsBetweenDate(LocalDate startDate, LocalDate endDate) {
        List<PlanedTrainingDTO> allPlanedTrainingDTOs = planedTrainingRepository.getAllTrainingsBetweenDate(startDate, endDate);
        List<Long> ids = allPlanedTrainingDTOs.stream().map(PlanedTrainingDTO::getId).toList();
        List<PlanedEmployeeDTO> allEmployees = planedEmployeeService.getAllEmployeesByTrainingIds(ids);
        Map<Long, List<PlanedEmployeeDTO>> mapEmployees = allEmployees.stream().collect(Collectors.groupingBy(PlanedEmployeeDTO::getIdPlaningDocument));
        allPlanedTrainingDTOs.forEach(eachDto -> {
            eachDto.setEmployees(mapEmployees.getOrDefault(eachDto.getId(), new ArrayList<>()));
        });
        return allPlanedTrainingDTOs;
    }

    @Override
    public List<PreviewEmployeeDTO> getAllAvailableEmployeeByTrainingId(Long id){
        PlanedTrainingDTO planedTrainingDTO = getPlanedTrainingsById(id);
        return planedEmployeeService.getAllEmployeesAvailableForTraining(planedTrainingDTO.getDate(), planedTrainingDTO.getTimeStart(), planedTrainingDTO.getTimeFinish(), new HashSet<>(planedTrainingDTO.getPositions()), planedTrainingDTO.getDocumentName());
    }

    @Override
    public PlanedTrainingDTO getPlanedTrainingsById(Long id) {
        return planedTrainingRepository.getPlanedTrainingById(id).orElseThrow(() -> new RuntimeException("Planed training not found"));
    }
    @Override
    public List<PlanedTrainingDTO> getAllPlanedTrainingsByDocumentName(String documentName) {
        return List.of();
    }

    @Override
    public void savePlanedTraining(PlaningTrainingRequest requestModel) {
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        DocumentDTO documents = documentsService.getDocumentByName(requestModel.getDocumentName()).orElseThrow();

        PlanedTraining entity = new PlanedTraining();
        entity.setDate(requestModel.getDate());
        entity.setTimeStart(requestModel.getStartTime());
        entity.setTimeFinish(requestModel.getEndTime());
        entity.setMaxCountEmployee(requestModel.getMaxSize());
        entity.setDocument(entityManager.getReference(Document.class, documents.getId()));
        entity.setPositions(requestModel.getPositions());
        entity.setNameTrainers(requestModel.getTrainers());
        entity.setPlace(requestModel.getPlace());
        entity.setDescription(requestModel.getDescription());
        entity.setIsAutoTraining(requestModel.getIsAutoTraining());
        entity.setUserId(currentUser.getUserId());

        planedTrainingRepository.save(entity);
    }

    @Override
    public void updatePlanedTraining(PlanedTrainingDTO planedTrainingDTO) {

    }

    @Override
    public void deletePlanedTraining(Long id) {
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        setCurrentUserId(currentUser.getUserId());
        planedTrainingRepository.deleteById(id);
    }

    private void setCurrentUserId(String userId) {
        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :userId, true)")
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
