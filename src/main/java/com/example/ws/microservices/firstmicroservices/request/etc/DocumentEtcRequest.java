package com.example.ws.microservices.firstmicroservices.request.etc;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DocumentEtcRequest {

    private String name;
    private String description;
    private String typeDocument;
    private String url;
    private Integer version;
    private LocalDate dateStart;
    private LocalDate dateFinish;
    private List<String> departments;
    private List<String> positions;
    private List<String> positionsAssistance;

}
