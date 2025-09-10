package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.etc;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeesupervisor.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.DocumentDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.TypeDocumentsDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.dto.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.dto.PositionDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.*;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Department;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.Position;
import com.example.ws.microservices.firstmicroservices.oldstructure.entity.etc.*;
import com.example.ws.microservices.firstmicroservices.oldstructure.repository.etc.DocumentRepository;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.etc.DocumentEtcRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.response.etc.DocumentConfigurationSave;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.etc.*;
import com.example.ws.microservices.firstmicroservices.common.security.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.common.security.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.service.UserService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.etc.*;
import com.example.ws.microservices.firstmicroservices.common.cache.redice.RedisCacheService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.vision.DepartmentService;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.vision.PositionService;
import com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.enums.ConnectionType;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentsServiceImpl implements DocumentsService {

    private final DocumentRepository documentRepository;
    private final TypeDocumentsService typeDocumentsService;
    private final RedisCacheService redisCacheService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final UserService userService;
    private final DocumentsDepartmentService documentsDepartmentService;
    private final DocumentsPositionService documentsPositionService;
    private final DocumentsPositionAssistanceService documentsPositionAssistanceService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DocumentConfigurationSave getConfigurationEtcSaveDocumentation(){
        return DocumentConfigurationSave.builder()
                .typeDocuments(typeDocumentsService.getAllTypeDocuments().stream().map(TypeDocumentsDTO::getName).toList())
                .departments(departmentService.getDepartmentsBySupervisorSite().stream().map(DepartmentDTO::getName).toList())
                .positions(positionService.getPositionsBySupervisorSite().stream().map(PositionDTO::getName).toList())
                .build();
    }

    @Override
    @Transactional
    public void saveDocument(DocumentEtcRequest documentEtcRequest) {

        Optional<Document> existing = documentRepository.findByName(documentEtcRequest.getName());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Another document with this name already exists");
        }

        CustomUserDetails users = new SecurityUtils().getCurrentUser();
        SupervisorAllInformationDTO usersDTO = userService.getSupervisorAllInformation(null, users.getUserId());

        if (getDocumentByName(documentEtcRequest.getName()).isPresent()) {
            throw new IllegalArgumentException("Document with this name already exists");
        }

        Integer typeId = typeDocumentsService.getAllTypeDocuments().stream()
                .filter(type -> type.getName().equalsIgnoreCase(documentEtcRequest.getTypeDocument()))
                .findFirst()
                .orElseThrow()
                .getId();

        List<DepartmentDTO> allDepartments = departmentService.getDepartmentsBySupervisorSite();
        List<PositionDTO> allPositions = positionService.getPositionsBySupervisorSite();

        List<DepartmentDTO> mappingDepartment = documentEtcRequest.getDepartments().stream()
                .map(name -> allDepartments.stream()
                        .filter(dep -> dep.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Department not found")))
                .distinct()
                .toList();

        List<PositionDTO> mappingPosition = documentEtcRequest.getPositions().stream()
                .map(name -> allPositions.stream()
                        .filter(pos -> pos.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Position not found")))
                .distinct()
                .toList();

        List<PositionDTO> mappingPositionAssistance = documentEtcRequest.getPositionsAssistance().stream()
                .map(name -> allPositions.stream()
                        .filter(pos -> pos.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Position assistance not found")))
                .distinct()
                .toList();

        Document newDocument = new Document();
        newDocument.setName(documentEtcRequest.getName());
        newDocument.setDescription(documentEtcRequest.getDescription());
        newDocument.setTypeDocument(entityManager.getReference(TypeDocument.class, typeId));
        newDocument.setUrl(documentEtcRequest.getUrl());
        newDocument.setVersion(documentEtcRequest.getVersion());
        newDocument.setDateStart(documentEtcRequest.getDateStart());
        newDocument.setDateFinish(documentEtcRequest.getDateFinish());
        newDocument.setDateLastUpd(LocalDate.now());
        newDocument.setUserId(users.getUserId());

        newDocument = documentRepository.save(newDocument);
        Long documentId = newDocument.getId();

        documentsPositionService.saveAll(mappingPosition.stream().map(position -> {
            DocumentsPosition docPos = new DocumentsPosition();
            docPos.setIdDocument(entityManager.getReference(Document.class, documentId.intValue()));
            docPos.setIdPosition(entityManager.getReference(Position.class, position.getId()));
            docPos.setUserId(users.getUserId());
            return docPos;
        }).toList());

        documentsPositionAssistanceService.saveAll(mappingPositionAssistance.stream().map(position -> {
            DocumentsPositionAssistance docPos = new DocumentsPositionAssistance();
            docPos.setIdDocument(entityManager.getReference(Document.class, documentId.intValue()));
            docPos.setIdPosition(entityManager.getReference(Position.class, position.getId()));
            docPos.setUserId(users.getUserId());
            return docPos;
        }).toList());

        documentsDepartmentService.saveAll(mappingDepartment.stream().map(department -> {
            DocumentsDepartment docDep = new DocumentsDepartment();
            docDep.setIdDocument(entityManager.getReference(Document.class, documentId.intValue()));
            docDep.setIdDepartment(entityManager.getReference(Department.class, department.getId()));
            docDep.setUserId(users.getUserId());
            return docDep;
        }).toList());

        List<String> allDepartmentsName = mappingDepartment.stream().map(DepartmentDTO::getName).toList();
        List<String> allPositionsName = mappingPosition.stream().map(PositionDTO::getName).toList();
        List<String> allPositionsAssistanceName = mappingPositionAssistance.stream().map(PositionDTO::getName).toList();

        DocumentDTO newDocumentDTO = DocumentDTO.builder()
                .id(documentId)
                .name(newDocument.getName())
                .description(newDocument.getDescription())
                .typeDocument(documentEtcRequest.getTypeDocument())
                .url(newDocument.getUrl())
                .version(newDocument.getVersion())
                .dateLastUpd(newDocument.getDateLastUpd())
                .dateStart(newDocument.getDateStart())
                .dateFinish(newDocument.getDateFinish())
                .userExpertis(usersDTO.getExpertis())
                .departments(allDepartmentsName)
                .positions(allPositionsName)
                .positionsAssistance(allPositionsAssistanceName)
                .build();

        redisCacheService.saveToHash("allDocuments", documentId.toString(), newDocumentDTO);

        redisCacheService.saveToHash("mappingDepartment", documentId.toString(), allDepartmentsName);
        redisCacheService.saveToHash("mappingPosition", documentId.toString(), allPositionsName);
        redisCacheService.saveToHash("mappingPositionAssistance", documentId.toString(), allPositionsAssistanceName);
    }

    @Override
    @Transactional
    public void updateDocument(Long documentId, DocumentEtcRequest documentEtcRequest) {

        Optional<Document> existing = documentRepository.findByName(documentEtcRequest.getName());
        if (existing.isPresent() && !existing.get().getId().equals(documentId)) {
            throw new IllegalArgumentException("Another document with this name already exists");
        }

        Document existingDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + documentId));

        SupervisorAllInformationDTO usersDTO = userService.getSupervisorAllInformation(null, new SecurityUtils().getCurrentUser().getUserId());

        existingDocument.setName(documentEtcRequest.getName());
        existingDocument.setDescription(documentEtcRequest.getDescription());
        existingDocument.setUrl(documentEtcRequest.getUrl());
        existingDocument.setVersion(documentEtcRequest.getVersion());
        existingDocument.setDateStart(documentEtcRequest.getDateStart());
        existingDocument.setDateFinish(documentEtcRequest.getDateFinish());
        existingDocument.setUserId(usersDTO.getUserId());
        existingDocument.setDateLastUpd(LocalDate.now());

        Integer typeId = typeDocumentsService.getAllTypeDocuments().stream()
                .filter(type -> type.getName().equalsIgnoreCase(documentEtcRequest.getTypeDocument()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("TypeDocument not found"))
                .getId();
        existingDocument.setTypeDocument(entityManager.getReference(TypeDocument.class, typeId));

        documentRepository.save(existingDocument);
        List<DepartmentDTO> departments = departmentService.getDepartmentsBySupervisorSite();
        Map<String, Integer> mapDepartments = departments.stream()
                .collect(Collectors.toMap(
                        DepartmentDTO::getName,
                        DepartmentDTO::getId,
                        (existingId, newId) -> existingId
                ));

        List<PositionDTO> positions = positionService.getPositionsBySupervisorSite();
        Map<String, Integer> mapPositions = positions.stream()
                .collect(Collectors.toMap(
                        PositionDTO::getName,
                        PositionDTO::getId,
                        (existingId, newId) -> existingId
                ));

        List<String> newPositions = documentEtcRequest.getPositions();
        if(!mapPositions.keySet().containsAll(newPositions)) {
            throw new IllegalArgumentException("Positions do not match");
        }
        Map<Long, List<String>> mapDocumentPosition = documentsPositionService.getMapDocumentsPosition();
        updateConnections(documentId, mapDocumentPosition.getOrDefault(documentId, new ArrayList<>()), newPositions, ConnectionType.POSITION, mapDepartments, mapPositions);

        List<String> newDepartments = documentEtcRequest.getDepartments();
        if(!mapDepartments.keySet().containsAll(newDepartments)) {
            throw new IllegalArgumentException("Departments do not match");
        }
        Map<Long, List<String>> mapDocumentDepartments = documentsDepartmentService.getMapDocumentsDepartment();
        updateConnections(documentId, mapDocumentDepartments.getOrDefault(documentId, new ArrayList<>()), newDepartments, ConnectionType.DEPARTMENT, mapDepartments, mapPositions);

        List<String> newPositionsAssistance = documentEtcRequest.getPositionsAssistance();
        if(!mapPositions.keySet().containsAll(newPositionsAssistance)) {
            throw new IllegalArgumentException("Positions assistance do not match");
        }
        Map<Long, List<String>> mapDocumentPositionAssistance = documentsPositionAssistanceService.getMapDocumentsPositionAssistance();
        updateConnections(documentId, mapDocumentPositionAssistance.getOrDefault(documentId, new ArrayList<>()), newPositionsAssistance, ConnectionType.POSITION_ASSISTANCE, mapDepartments, mapPositions);

        DocumentDTO updatedDTO = DocumentDTO.builder()
                .id(documentId)
                .name(documentEtcRequest.getName())
                .description(documentEtcRequest.getDescription())
                .typeDocument(documentEtcRequest.getTypeDocument())
                .url(documentEtcRequest.getUrl())
                .version(documentEtcRequest.getVersion())
                .dateLastUpd(LocalDate.now())
                .dateStart(documentEtcRequest.getDateStart())
                .dateFinish(documentEtcRequest.getDateFinish())
                .userExpertis(usersDTO.getExpertis())
                .departments(newDepartments)
                .positions(newPositions)
                .positionsAssistance(newPositionsAssistance)
                .build();

        redisCacheService.saveToHash("allDocuments", documentId.toString(), updatedDTO);
        redisCacheService.saveToHash("mappingDepartment", documentId.toString(), newDepartments);
        redisCacheService.saveToHash("mappingPosition", documentId.toString(), newPositions);
        redisCacheService.saveToHash("mappingPositionAssistance", documentId.toString(), newPositionsAssistance);
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        setCurrentUserId(currentUser.getUserId());

        Document existing = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Document not fount with ID: " + documentId));

        documentRepository.delete(existing);

        String key = documentId.toString();
        redisCacheService.deleteFromHash("allDocuments", key);
        redisCacheService.deleteFromHash("mappingDepartment", key);
        redisCacheService.deleteFromHash("mappingPosition", key);
        redisCacheService.deleteFromHash("mappingPositionAssistance", key);
    }

    @Override
    public Optional<DocumentDTO> getDocumentByName(String documentName) {
        return getAllDocuments().stream().filter(document -> document.getName().equals(documentName)).findFirst();
    }

    @Override
    public List<DocumentDTO> getAllDocuments() {
        Map<String, DocumentDTO> cachedDocuments = redisCacheService.getAllFromHash(
                "allDocuments", new TypeReference<DocumentDTO>() {}
        );

        if (!cachedDocuments.isEmpty()) {
            return new ArrayList<>(cachedDocuments.values());
        }

        Map<Long, List<String>> mappingDepartment = documentsDepartmentService.getMapDocumentsDepartment();
        Map<Long, List<String>> mappingPosition = documentsPositionService.getMapDocumentsPosition();
        Map<Long, List<String>> mappingPositionAssistance = documentsPositionAssistanceService.getMapDocumentsPositionAssistance();

        List<DocumentDTO> allDocumentList = documentRepository.findAllCustom();

        allDocumentList.forEach(document -> {
            document.setDepartments(mappingDepartment.getOrDefault(document.getId(), new ArrayList<>()));
            document.setPositions(mappingPosition.getOrDefault(document.getId(), new ArrayList<>()));
            document.setPositionsAssistance(mappingPositionAssistance.getOrDefault(document.getId(), new ArrayList<>()));

            redisCacheService.saveToHash("allDocuments", document.getId().toString(), document);
        });

        return allDocumentList;
    }

    @Override
    public List<DocumentDTO> getAllDocumentsByDepartment(String departmentName) {
        return getAllDocuments().stream()
                .filter(doc -> doc.getDepartments() != null && doc.getDepartments().contains(departmentName))
                .toList();
    }

    @Override
    public List<DocumentDTO> getAllDocumentsByPosition(String positionName) {
        return getAllDocuments().stream()
                .filter(doc -> doc.getPositions() != null && doc.getPositions().contains(positionName))
                .toList();
    }

    @Override
    public List<DocumentDTO> getAllDocumentsFilterByDepartmentAndPosition(String departmentName, String positionName) {
        return getAllDocuments().stream()
                .filter(doc -> doc.getPositions() != null && doc.getPositions().contains(positionName))
                .filter(doc -> doc.getDepartments() != null && doc.getDepartments().contains(departmentName))
                .toList();
    }

    private void updateConnections(Long documentId, List<String> currentList, List<String> newList, ConnectionType type, Map<String, Integer> departments, Map<String, Integer>  positions) {
        Set<String> currentSet = new HashSet<>(currentList);
        Set<String> newSet = new HashSet<>(newList);

        Set<String> toAdd = new HashSet<>(newSet);
        toAdd.removeAll(currentSet);

        Set<String> toRemove = new HashSet<>(currentSet);
        toRemove.removeAll(newSet);

        if (!toRemove.isEmpty()) {
            List<Long> idsToRemove = switch (type) {
                case DEPARTMENT -> toRemove.stream().map(el -> departments.get(el).longValue()).collect(Collectors.toList());
                case POSITION -> toRemove.stream().map(el -> positions.get(el).longValue()).collect(Collectors.toList());
                case POSITION_ASSISTANCE -> toRemove.stream().map(el -> positions.get(el).longValue()).collect(Collectors.toList());
            };
            switch (type) {
                case DEPARTMENT -> documentsDepartmentService.deleteByDocumentIdAndDepartmentIds(documentId, idsToRemove);
                case POSITION -> documentsPositionService.deleteByDocumentIdAndPositionIds(documentId, idsToRemove);
                case POSITION_ASSISTANCE -> documentsPositionAssistanceService.deleteByDocumentIdAndPositionAssistanceIds(documentId, idsToRemove);
            }
        }

        if (!toAdd.isEmpty()) {
            switch (type) {
                case DEPARTMENT -> {
                    List<DocumentsDepartment> entitiesToSave = toAdd.stream().map(el -> {
                        DocumentsDepartment docDep = new DocumentsDepartment();
                        docDep.setIdDocument(entityManager.getReference(Document.class, documentId.intValue()));
                        docDep.setIdDepartment(entityManager.getReference(Department.class, departments.get(el)));
                        return docDep;
                    }).collect(Collectors.toList());
                    documentsDepartmentService.saveAll(entitiesToSave);
                }
                case POSITION -> {
                    List<DocumentsPosition> entitiesToSave = toAdd.stream().map(el -> {
                        DocumentsPosition docPos = new DocumentsPosition();
                        docPos.setIdDocument(entityManager.getReference(Document.class, documentId.intValue()));
                        docPos.setIdPosition(entityManager.getReference(Position.class, positions.get(el)));
                        return docPos;
                    }).collect(Collectors.toList());
                    documentsPositionService.saveAll(entitiesToSave);
                }
                case POSITION_ASSISTANCE -> {
                    List<DocumentsPositionAssistance> entitiesToSave = toAdd.stream().map(el -> {
                        DocumentsPositionAssistance docAssistance = new DocumentsPositionAssistance();
                        docAssistance.setIdDocument(entityManager.getReference(Document.class, documentId.intValue()));
                        docAssistance.setIdPosition(entityManager.getReference(Position.class, positions.get(el)));
                        return docAssistance;
                    }).collect(Collectors.toList());
                    documentsPositionAssistanceService.saveAll(entitiesToSave);
                }
            }
        }
    }

    private void setCurrentUserId(String userId) {
        entityManager.createNativeQuery("SELECT set_config('app.current_user_id', :userId, true)")
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
