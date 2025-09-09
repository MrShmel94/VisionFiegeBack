package com.example.ws.microservices.firstmicroservices.oldstructure.response;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.dto.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConfigurationRegistrationDTO {

    private String siteName;
    private List<AgencyDTO> agencies;
    private List<CountryDTO> countries;
    private List<DepartmentDTO> departments;
    private List<PositionDTO> positions;
    private List<ShiftDTO> shifts;
    private List<SiteDTO> sites;
    private List<TeamDTO> teams;

}
