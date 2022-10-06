package com.sketch.brain.backend.global.error.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCodeImpl implements ErrorCode{
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST,"Invalid Parameter included"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND,"Resource not exists"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error"),
    INVALID_ARGUMENT_TYPE(HttpStatus.BAD_REQUEST,"Invalid Parameter type included."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Request Method type is not allowed.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
