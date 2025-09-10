package com.example.ws.microservices.firstmicroservices.domain.employeedata.employeesupervisor.dto;

import com.example.ws.microservices.firstmicroservices.domain.usermanagement.userrole.dto.UserRoleDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.email.EmailDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.contact.phone.PhoneDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorAllInformationDTO {

    private Long id;
    private String expertis;
    private String brCode;
    private String firstName;
    private String lastName;
    private Boolean isWork;
    private String sex;
    private String siteName;
    private String shiftName;
    private String departmentName;
    private String teamName;
    private String positionName;
    private String agencyName;
    private String userId;
    private String email;
    private Boolean isVerified;
    private Boolean emailVerificationStatus;
    private Boolean isCanHasAccount;
    private Boolean isSupervisor;
    private LocalDate validToAccount;
    private LocalDate validFromAccount;
    private String note;
    private LocalDate dateStartContract;
    private LocalDate dateFinishContract;
    private LocalDate dateBhpNow;
    private LocalDate dateBhpFuture;
    private LocalDate dateAdrNow;
    private LocalDate dateAdrFuture;

    @Builder.Default
    private List<UserRoleDTO> roles = new ArrayList<>();
    @Builder.Default
    private List<String> employeeList = new ArrayList<>();
    @Builder.Default
    private List<PhoneDTO> phones = new ArrayList<>();
    @Builder.Default
    private List<EmailDTO> emails = new ArrayList<>();

    //FOR JPQL
    public SupervisorAllInformationDTO(
            Long id, String expertis, String brCode, String firstName, String lastName, Boolean isWork,
            String sex, String siteName, String shiftName, String departmentName, String teamName,
            String positionName, String agencyName, String userId, String email, Boolean isVerified,
            Boolean emailVerificationStatus, Boolean isCanHasAccount, Boolean isSupervisor, LocalDate validToAccount, LocalDate validFromAccount, String note, LocalDate dateStartContract, LocalDate dateFinishContract,
            LocalDate dateBhpNow, LocalDate dateBhpFuture, LocalDate dateAdrNow, LocalDate dateAdrFuture
    ) {
        this.id = id;
        this.expertis = expertis;
        this.brCode = brCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isWork = isWork;
        this.sex = sex;
        this.siteName = siteName;
        this.shiftName = shiftName;
        this.departmentName = departmentName;
        this.teamName = teamName;
        this.positionName = positionName;
        this.agencyName = agencyName;
        this.userId = userId;
        this.email = email;
        this.isSupervisor = isSupervisor;
        this.isVerified = isVerified;
        this.emailVerificationStatus = emailVerificationStatus;
        this.isCanHasAccount = isCanHasAccount;
        this.validToAccount = validToAccount;
        this.validFromAccount = validFromAccount;
        this.note = note;
        this.dateStartContract = dateStartContract;
        this.dateFinishContract = dateFinishContract;
        this.dateBhpNow = dateBhpNow;
        this.dateBhpFuture = dateBhpFuture;
        this.dateAdrNow = dateAdrNow;
        this.dateAdrFuture = dateAdrFuture;
    }

}
