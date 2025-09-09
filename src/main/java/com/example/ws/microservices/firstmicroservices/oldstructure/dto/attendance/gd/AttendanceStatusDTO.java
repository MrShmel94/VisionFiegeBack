package com.example.ws.microservices.firstmicroservices.oldstructure.dto.attendance.gd;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceStatusDTO {
    private Integer id;
    @JsonInclude()
    private String statusCode;
    private String statusName;
    @JsonInclude()
    private String displayFront;
    private String description;
    private String color;
}
