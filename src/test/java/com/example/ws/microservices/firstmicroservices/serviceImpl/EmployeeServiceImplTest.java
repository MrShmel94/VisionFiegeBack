package com.example.ws.microservices.firstmicroservices.serviceImpl;

import com.example.ws.microservices.firstmicroservices.dto.EmployeeDTO;
import com.example.ws.microservices.firstmicroservices.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getUsersByExpertis_ShouldReturnEmployee() {
        String expertis = "Expertis_22";
        EmployeeDTO employeeDTO = EmployeeDTO.builder()
                .id(1L)
                .expertis(expertis)
                .firstName("John")
                .lastName("Doe")
                .isWork(true)
                .sex("M")
                .siteName("Site A")
                .shiftName("Shift 1")
                .departmentName("Department A")
                .teamName("Team A")
                .positionName("Position A")
                .agencyName("Agency A")
                .build();


        when(employeeRepository.findEmployeeByExpertis(expertis)).thenReturn(Optional.of(employeeDTO));

        Optional<EmployeeDTO> result = employeeService.getUsersByExpertis(expertis);

        assertTrue(result.isPresent());
        assertEquals(employeeDTO, result.get());
    }

    @Test
    void shouldReturnEmptyOptional_whenExpertisDoesNotExist() {
        @org.jetbrains.annotations.NotNull String givenExpertis = "Invalid";
        given(employeeRepository.findEmployeeByExpertis(givenExpertis)).willReturn(Optional.empty());

        EmployeeServiceImpl employeeService = new EmployeeServiceImpl(employeeRepository);
        Optional<EmployeeDTO> actualUserOptional = employeeService.getUsersByExpertis(givenExpertis);

        then(employeeRepository).should().findEmployeeByExpertis(givenExpertis);
        assertTrue(actualUserOptional.isEmpty());
    }

}