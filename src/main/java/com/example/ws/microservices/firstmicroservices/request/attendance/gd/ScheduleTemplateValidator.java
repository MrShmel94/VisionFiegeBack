package com.example.ws.microservices.firstmicroservices.request.attendance.gd;

import com.example.ws.microservices.firstmicroservices.domain.attendace.shifttimework.ShiftTimeWorkDTO;
import com.example.ws.microservices.firstmicroservices.dto.attendance.gd.AttendanceStatusDTO;
import com.example.ws.microservices.firstmicroservices.response.attendance.gd.ResponseScheduleTemplate;
import com.example.ws.microservices.firstmicroservices.service.attendance.gd.ScheduleTemplateService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ScheduleTemplateValidator implements ConstraintValidator<ValidScheduleTemplate, ScheduleTemplateRequest> {

    private final ScheduleTemplateService scheduleTemplateService;

    @Override
    public boolean isValid(ScheduleTemplateRequest request, ConstraintValidatorContext context) {

        if (request == null) {
            return true;
        }

        YearMonth period = request.getPeriod();
        Map<String, DayScheduleRequest> schedule = request.getSchedule();
        if (period == null || CollectionUtils.isEmpty(schedule)) {
            return true;
        }

        int daysInMonth = period.lengthOfMonth();
        List<Integer> missingDays = new ArrayList<>();
        List<Integer> invalidDays = new ArrayList<>();

        ResponseScheduleTemplate template = scheduleTemplateService.getScheduleTemplate();
        Set<String> hashShiftCode = template.getShiftTimeWork().stream().map(ShiftTimeWorkDTO::getShiftCode).collect(Collectors.toSet());
        Set<String> hashStatusCode = template.getAttendanceStatus().stream().map(AttendanceStatusDTO::getStatusCode).collect(Collectors.toSet());

        for (int day = 1; day <= daysInMonth; day++) {
            String dayKey = String.valueOf(day);
            if (!schedule.containsKey(dayKey)) {
                missingDays.add(day);
            }else{
                DayScheduleRequest dayScheduleRequest = schedule.get(dayKey);
                String requestShiftCode = dayScheduleRequest.getShiftCode();
                String requestStatusCode = dayScheduleRequest.getStatusCode();

                if(!hashShiftCode.contains(requestShiftCode) || !hashStatusCode.contains(requestStatusCode)){
                    invalidDays.add(day);
                }
            }
        }

        boolean valid = true;
        context.disableDefaultConstraintViolation();

        if (!missingDays.isEmpty()) {
            context.buildConstraintViolationWithTemplate(
                            "Missing schedule entries for days: " + missingDays)
                    .addPropertyNode("schedule")
                    .addConstraintViolation();
            valid = false;
        }

        if (!invalidDays.isEmpty()) {
            context.buildConstraintViolationWithTemplate(
                            "Invalid schedule entries for days: " + invalidDays + ". Please ensure that shift and status codes are correct.")
                    .addPropertyNode("schedule")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }

}
