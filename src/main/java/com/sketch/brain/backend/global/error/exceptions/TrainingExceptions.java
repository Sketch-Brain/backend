package com.sketch.brain.backend.global.error.exceptions;

import lombok.Getter;

@Getter
public class TrainingExceptions extends RuntimeException{

    private final TrainingErrorCodeImpl errorCode;

    public TrainingExceptions(TrainingErrorCodeImpl errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TrainingExceptions(String message, TrainingErrorCodeImpl errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
