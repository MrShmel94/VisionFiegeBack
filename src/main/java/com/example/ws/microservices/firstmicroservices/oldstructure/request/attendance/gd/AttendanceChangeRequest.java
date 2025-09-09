package com.example.ws.microservices.firstmicroservices.oldstructure.request.attendance.gd;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceChangeRequest {

    @NotNull
    private Long attendanceId;

    @NotNull
    private Long employeeId;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @NotNull
    @NotBlank
    private String shiftCode;

    @NotNull
    @NotBlank
    private String siteName;

    @NotNull
    @NotBlank
    private String statusCode;

    @NotNull
    @NotBlank
    private String departmentName;

    @NotNull
    private Double houseWorked;
    @NotNull
    private String comment;

    @NotNull
    @NotBlank
    private String topicDate;

    @NotNull
    private Integer dayIndex;

}
