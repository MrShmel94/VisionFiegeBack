package com.example.ws.microservices.firstmicroservices.oldstructure.response;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.agency.AgencyDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.country.CountryDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.department.DepartmentDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.position.PositionDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.shift.ShiftDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.site.SiteDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.team.TeamDTO;
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
