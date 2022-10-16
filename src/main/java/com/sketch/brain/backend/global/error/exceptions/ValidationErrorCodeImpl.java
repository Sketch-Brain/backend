package com.sketch.brain.backend.global.error.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ValidationErrorCodeImpl implements ErrorCode{
    REQUIRED_LAYER_ATTRIBUTE_IS_EMPTY(HttpStatus.BAD_REQUEST,"Some of Must Required layer attributes has a problem.");

    private final HttpStatus httpStatus;
    private final String message;
}
