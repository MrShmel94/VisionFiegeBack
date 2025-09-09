package com.example.ws.microservices.firstmicroservices.oldstructure.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class CreateEmployeeRequest {

    @NotBlank(message = "Expertis is required")
    @Size(min = 5, message = "Expertis must be at least 5 characters long")
    @Pattern(regexp = "\\d+", message = "Expertis must contain only digits")
    private String expertis;

    @Pattern(regexp = "BR-\\d+", message = "BR code must start with 'BR' followed by digits")
    private String brCode = null;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^([MF])$", message = "Sex must be either 'M' or 'F'")
    private String sex;

    @NotBlank(message = "Site is required")
    private String site;

    @NotBlank(message = "Shift is required")
    private String shift;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Team is required")
    private String team;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Position is required")
    private String position;

    @NotBlank(message = "Agency is required")
    private String agency;

    private Boolean isWork = true;

    private Boolean isCanHasAccount = false;

    private Boolean isSupervisor = false;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime validToAccount = LocalDateTime.now().minusSeconds(1);
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime validFromAccount = LocalDateTime.now();

    private String note = "";

    @NotNull(message = "Date Start Contract should be!")
    private LocalDate dateStartContract;

    @NotNull(message = "Date Start Contract should be!")
    private LocalDate dateFinishContract;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateBhpNow = null;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateBhpFuture = null;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateAdrNow = null;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateAdrFuture = null;

    private Double fte = 1D;
}
