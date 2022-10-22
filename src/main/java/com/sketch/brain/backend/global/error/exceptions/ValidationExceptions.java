package com.sketch.brain.backend.global.error.exceptions;

import com.sketch.brain.backend.global.error.ArgumentError;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationExceptions extends RuntimeException{

    private final ValidationErrorCodeImpl errorCode;
    private final List<ArgumentError> argumentError;

    public ValidationExceptions(ValidationErrorCodeImpl errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.argumentError = new ArrayList<>();
    }

    public ValidationExceptions(String message, ValidationErrorCodeImpl errorCode){
        super(message);
        this.errorCode = errorCode;
        this.argumentError = new ArrayList<>();
    }

    public ValidationExceptions(ValidationErrorCodeImpl errorCode, List<ArgumentError> argumentError){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.argumentError = argumentError;
    }
}
