package com.sketch.brain.backend.global.error.exceptions;

import com.sketch.brain.backend.global.error.ArgumentError;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ContainerExceptions extends RuntimeException{

    private final ContainerErrorCodeImpl errorCode;
    private final List<ArgumentError> argumentError;

    public ContainerExceptions(ContainerErrorCodeImpl errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.argumentError = new ArrayList<>();
    }

    public ContainerExceptions(String message, ContainerErrorCodeImpl errorCode){
        super(message);
        this.errorCode = errorCode;
        this.argumentError = new ArrayList<>();
    }

    public ContainerExceptions(ContainerErrorCodeImpl errorCode, List<ArgumentError> argumentError){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.argumentError = argumentError;
    }
}
