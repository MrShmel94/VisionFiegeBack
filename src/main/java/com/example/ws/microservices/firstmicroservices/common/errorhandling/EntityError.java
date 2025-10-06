package com.example.ws.microservices.firstmicroservices.common.errorhandling;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum EntityError {
    ROUTE_NOT_FOUND("ROUTE_NOT_FOUND", "error.route.not_found", HttpStatus.NOT_FOUND),
    EMPLOYEE_CARD_NOT_FOUND("EMPLOYEE_CARD_NOT_FOUND", "error.employee_card.not_found", HttpStatus.NOT_FOUND),
    EMPLOYEE_CARD_EXISTS("EMPLOYEE_CARD_EXISTS", "error.employee_card.exists", HttpStatus.CONFLICT),
    BUS_BOARDING_NOT_FOUND("BUS_BOARDING_NOT_FOUND", "error.bus_boarding.not_found", HttpStatus.NOT_FOUND);

    public final String apiCode;
    public final String msgKey;
    public final HttpStatus status;
}
