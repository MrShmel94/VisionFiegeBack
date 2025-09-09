package com.example.ws.microservices.firstmicroservices.oldstructure.service;

import com.example.ws.microservices.firstmicroservices.domain.attendace.shifttimework.ShiftTimeWorkDTO;

import java.util.List;

public interface ShiftTimeWorkService {

    List<ShiftTimeWorkDTO> getShiftTimeWorkByNameSite(String siteName);
}
