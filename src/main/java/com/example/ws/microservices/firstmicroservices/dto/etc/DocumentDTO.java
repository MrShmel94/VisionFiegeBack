package com.example.ws.microservices.firstmicroservices.dto.etc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {

    private Long id;
    private String name;
    private String description;
    private String typeDocument;
    private String url;
    private Integer version;
    private LocalDate dateLastUpd;
    private LocalDate dateStart;
    private LocalDate dateFinish;
    private String userExpertis;
    List<String> departments;
    List<String> positions;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> positionsAssistance;

    //FOR JPQL
    public DocumentDTO(Long id, String name, String description, String typeDocument, String url,
                       Integer version, LocalDate dateLastUpd,
                       LocalDate dateStart, LocalDate dateFinish, String userExpertis) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.typeDocument = typeDocument;
        this.url = url;
        this.version = version;
        this.dateLastUpd = dateLastUpd;
        this.dateStart = dateStart;
        this.dateFinish = dateFinish;
        this.userExpertis = userExpertis;
    }

}
