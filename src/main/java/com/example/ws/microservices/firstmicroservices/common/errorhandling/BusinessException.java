package com.example.ws.microservices.firstmicroservices.common.errorhandling;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private final String messageKey;
    private final Object[] messageArgs;

    // main constructor
    public BusinessException(String code, HttpStatus status, String messageKey, Object... messageArgs) {
        super(code);
        this.code = code;
        this.status = status;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public BusinessException(EntityError error, Object... messageArgs) {
        this(error.apiCode, error.status, error.msgKey, messageArgs);
    }
}
