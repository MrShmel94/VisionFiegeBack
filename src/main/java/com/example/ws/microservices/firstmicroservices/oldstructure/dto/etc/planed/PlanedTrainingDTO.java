package com.example.ws.microservices.firstmicroservices.oldstructure.dto.etc.planed;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanedTrainingDTO {

    private Long id;
    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeFinish;
    private Integer maxCountEmployee;
    private String documentName;
    private List<String> positions;
    private List<String> nameTrainers;
    private String place;
    private String description;
    private String fullNameCreator;
    @JsonInclude()
    private List<PlanedEmployeeDTO> employees = new ArrayList<>();;

    //FOR JPQL
    public PlanedTrainingDTO(Long id, LocalDate date, LocalTime timeStart, LocalTime timeFinish, Integer maxCountEmployee,
     String documentName,
     List<String> positions,
     List<String> nameTrainers,
     String place,
     String description,
     String fullNameCreator
     ){
        this.id = id;
        this.date = date;
        this.timeStart = timeStart;
        this.timeFinish = timeFinish;
        this.maxCountEmployee = maxCountEmployee;
        this.documentName = documentName;
        this.positions = positions;
        this.nameTrainers = nameTrainers;
        this.place = place;
        this.description = description;
        this.fullNameCreator = fullNameCreator;
    }

}
