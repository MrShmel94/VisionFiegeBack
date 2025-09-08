package com.example.ws.microservices.firstmicroservices.serviceImpl.etc;

import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.dto.etc.TypeDocumentsDTO;
import com.example.ws.microservices.firstmicroservices.entity.etc.TypeDocument;
import com.example.ws.microservices.firstmicroservices.repository.etc.TypeDocumentsRepository;
import com.example.ws.microservices.firstmicroservices.secure.CustomUserDetails;
import com.example.ws.microservices.firstmicroservices.secure.SecurityUtils;
import com.example.ws.microservices.firstmicroservices.domain.usermanagement.user.UserService;
import com.example.ws.microservices.firstmicroservices.service.etc.TypeDocumentsService;
import com.example.ws.microservices.firstmicroservices.service.redice.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TypeDocumentsServiceImpl implements TypeDocumentsService {

    private final TypeDocumentsRepository repository;
    private final RedisCacheService redisCacheService;
    private final UserService userService;

    @Override
    public void saveTypeDocument(TypeDocumentsDTO typeDocumentsDTO) {
        List<TypeDocumentsDTO> allTypesDTO = getAllTypeDocuments();

        if(allTypesDTO.stream().anyMatch(dto -> dto.getName().equalsIgnoreCase(typeDocumentsDTO.getName()))){
            throw new RuntimeException("Type document already exists");
        }

        log.info("Fetching supervisors for the current user.");
        CustomUserDetails currentUser = new SecurityUtils().getCurrentUser();
        log.debug("Current user ID: {}", currentUser.getUserId());

        SupervisorAllInformationDTO allInformation = userService.getSupervisorAllInformation(null, currentUser.getUserId());
        log.debug("Retrieved supervisor information for user: {} with siteName: {}", currentUser.getUserId(), allInformation.getSiteName());

        TypeDocument entity = new TypeDocument();
        entity.setName(typeDocumentsDTO.getName());
        entity.setDescription(typeDocumentsDTO.getDescription());
        entity.setUserId(allInformation.getUserId());

        repository.save(entity);

        allTypesDTO.add(typeDocumentsDTO);
        redisCacheService.saveToCache("typeDocuments", allTypesDTO);
    }

    @Override
    public List<TypeDocumentsDTO> getAllTypeDocuments() {
        List<TypeDocumentsDTO> typeDocumentsDTO = redisCacheService
                .getFromCache("typeDocuments", new TypeReference<List<TypeDocumentsDTO>>() {
                }).orElseGet(() -> {
                   List<TypeDocumentsDTO> allTypeDocument = repository.findAll().stream()
                           .map(eachObject -> {
                               return TypeDocumentsDTO.builder()
                                       .id(eachObject.getId())
                                       .name(eachObject.getName())
                                       .description(eachObject.getDescription())
                                       .createdTime(eachObject.getCreatedDate())
                                       .userId(eachObject.getUserId())
                                       .build();
                           }).toList();

                   redisCacheService.saveToCache("typeDocuments", allTypeDocument);
                   return allTypeDocument;
                });
        return typeDocumentsDTO;
    }
}
