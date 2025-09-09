package com.example.ws.microservices.firstmicroservices.oldstructure.controller;

import com.example.ws.microservices.firstmicroservices.oldstructure.request.etc.DocumentEtcRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.etc.PlaningTrainingRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.etc.MultiPlanedEmployeeRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.etc.DocumentsService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.etc.planed.PlanedEmployeeService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.etc.planed.PlanedTrainingService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.vision.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/etc")
public class EtcController {

    private final DocumentsService documentsService;
    private final DepartmentService departmentService;
    private final PlanedTrainingService planedTrainingService;
    private final PlanedEmployeeService planedEmployeeService;

    @PostMapping(path = "/saveDocument", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> saveDocument(@RequestBody @Valid DocumentEtcRequest documentEtcRequest) {
        documentsService.saveDocument(documentEtcRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/updateDocument/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> updateDocument(@PathVariable("id") Long id, @RequestBody @Valid DocumentEtcRequest documentEtcRequest) {
        documentsService.updateDocument(id, documentEtcRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/configDocument")
    ResponseEntity<?> getConfigSaveDocument() {
        return ResponseEntity.ok(documentsService.getConfigurationEtcSaveDocumentation());
    }

    @GetMapping(path = "/departments")
    ResponseEntity<?> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getDepartmentsBySupervisorSite());
    }

    @GetMapping()
    ResponseEntity<?> getAllDepartments(@RequestParam String department) {
        return ResponseEntity.ok(documentsService.getAllDocumentsByDepartment(department));
    }

    @PostMapping(path = "/savePlaningTraining", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> savePlaningTraining(@RequestBody @Valid PlaningTrainingRequest request) {
        planedTrainingService.savePlanedTraining(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/getAllActivePlaning")
    ResponseEntity<?> getAllActivePlaning(@RequestParam("start") LocalDate start,
                                          @RequestParam("end") LocalDate end) {
        return ResponseEntity.ok(planedTrainingService.getAllPlanedTrainingsBetweenDate(start, end));
    }

    @GetMapping(path = "/deleteTraining")
    ResponseEntity<?> deletePlaningTraining(@RequestParam("id") Long id) {
        planedTrainingService.deletePlanedTraining(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/deleteDocument")
    ResponseEntity<?> deleteDocument(@RequestParam("id") Long id) {
        documentsService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/availableEmployee")
    ResponseEntity<?> getAllAvailableEmployeeByPlaningId(@RequestParam("id") Long planingId) {
        return ResponseEntity.ok(planedTrainingService.getAllAvailableEmployeeByTrainingId(planingId));
    }

    @PostMapping(path = "/setPlaningEmployees")
    ResponseEntity<?> setPlaningEmployees(@RequestBody @Valid MultiPlanedEmployeeRequest request){
        planedEmployeeService.savePlanedEmployeeTraining(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/setPresentEmployees")
    ResponseEntity<?> setPresentEmployees(@RequestBody @Valid MultiPlanedEmployeeRequest request){
        planedEmployeeService.setPlanedEmployeeTraining(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/deletePlaningEmployees")
    ResponseEntity<?> deletePlaningEmployees(@RequestBody @Valid MultiPlanedEmployeeRequest request){
        planedEmployeeService.deletePlanedEmployeeTraining(request);
        return ResponseEntity.ok().build();
    }
}
