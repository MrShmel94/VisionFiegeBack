package com.example.ws.microservices.firstmicroservices.oldstructure.controller.webSocket;

import com.example.ws.microservices.firstmicroservices.oldstructure.request.attendance.gd.AttendanceChangeRequest;
import com.example.ws.microservices.firstmicroservices.oldstructure.request.attendance.gd.GetAttendanceList;
import com.example.ws.microservices.firstmicroservices.oldstructure.response.attendance.gd.EmployeeAttendanceDTO;
import com.example.ws.microservices.firstmicroservices.oldstructure.service.attendance.gd.AttendanceService;
import com.example.ws.microservices.firstmicroservices.common.utils.CompressionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AttendanceWebSocketController {

    private final AttendanceService attendanceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @MessageMapping("/getAttendanceList")
    public void getAttendanceList(GetAttendanceList request, Principal principal) {
        System.out.println("Запрос от пользователя: " + principal.getName());
        System.out.printf("Start date topic -> %s\nEnd date topic -> %s\n", request.getStartDate(), request.getEndDate());

        List<EmployeeAttendanceDTO> list = attendanceService.getAttendanceDTOByEmployeeIdAndDate(
                null,
                request.getStartDate(),
                request.getEndDate()
        );

        String json = objectMapper.writeValueAsString(list);
        String compressed = CompressionUtils.compress(json);

        String topic = "/topic/attendanceList/" + request.getStartDate() + "_" + request.getEndDate();
        messagingTemplate.convertAndSend(topic, compressed);
    }

    @MessageMapping("/updateAttendanceDay")
    public void updateAttendanceDay(AttendanceChangeRequest updatedDto, Principal principal) {
        attendanceService.updateAttendanceDay(updatedDto, principal);
    }

    @MessageMapping("/updateAttendanceDayBulk")
    public void updateAttendanceDay(List<AttendanceChangeRequest> updatedDto, Principal principal) {
        attendanceService.updateAttendanceBulk(updatedDto, principal);
    }
}
