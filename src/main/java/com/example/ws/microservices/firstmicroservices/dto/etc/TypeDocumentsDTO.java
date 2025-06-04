package com.example.ws.microservices.firstmicroservices.dto.etc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class TypeDocumentsDTO {

    private Integer id;
    private String name;
    private String description;
    private LocalDate createdTime;
    private String userId;

}
