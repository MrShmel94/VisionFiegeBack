package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.dto.SupervisorAllInformationDTO;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisor;
import com.example.ws.microservices.firstmicroservices.entity.EmployeeSupervisorId;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeSupervisorRepository;
import com.example.ws.microservices.firstmicroservices.request.RequestSetEmployeeToSupervisor;
import com.example.ws.microservices.firstmicroservices.service.EmployeeSupervisorService;
import com.example.ws.microservices.firstmicroservices.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeSupervisorServiceImpl implements EmployeeSupervisorService {

    private final EmployeeSupervisorRepository repository;
    private final UserService userService;

    public void addAccess(Long employeeId, String supervisorExpertis, LocalDateTime validTo) {
        EmployeeSupervisor access = new EmployeeSupervisor();
        access.setEmployeeId(employeeId);
        access.setValidFrom(LocalDateTime.now());
        access.setValidTo(validTo);
        repository.save(access);
    }

    @Override
    public List<String> addAccess(List<RequestSetEmployeeToSupervisor> employees) {
        List<String> wrongExpertis = new ArrayList<>();
        employees.forEach(request -> {
            SupervisorAllInformationDTO supervisor = userService.getSupervisorAllInformation(request.getSupervisorExpertis(), null);
            if(supervisor.getIsSupervisor()){
                List<String> listExpertis = new ArrayList<>();
                List<LocalDateTime> listDateTo = new ArrayList<>();
                List<LocalDateTime> listDateFrom = new ArrayList<>();

                request.getRequests().forEach((key, value) -> {
                    listExpertis.add(key);
                    listDateTo.add(value.getValidTo());
                    listDateFrom.add(value.getValidFrom());
                })
                ;
                List<String> wrongExpertisNow = repository.insertEmployeeSupervisors(
                        listExpertis.toArray(new String[0]),
                        listDateFrom.toArray(new LocalDateTime[0]),
                        listDateTo.toArray(new LocalDateTime[0]),
                        supervisor.getId()
                );

                wrongExpertis.addAll(wrongExpertisNow);
            }
        });
        return wrongExpertis;
    }

    public void revokeAccess(Long employeeId, String supervisorExpertis) {
        repository.deleteById(new EmployeeSupervisorId(employeeId, supervisorExpertis));
    }

    public List<EmployeeSupervisor> getActiveAccesses() {
        return repository.findActiveAccesses(LocalDateTime.now());
    }

    public List<EmployeeSupervisor> getExpiredAccesses() {
        return repository.findExpiredAccesses(LocalDateTime.now());
    }

    public List<EmployeeSupervisor> findByEmployeeId(Long employeeId) {
        return repository.findByEmployeeId(employeeId);
    }
}
