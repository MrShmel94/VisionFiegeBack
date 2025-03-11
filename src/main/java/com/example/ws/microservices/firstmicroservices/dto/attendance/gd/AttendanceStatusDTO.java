package com.example.ws.microservices.firstmicroservices.dto.attendance.gd;

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
    private String statusCode;
    private String statusName;
    private String description;
}
