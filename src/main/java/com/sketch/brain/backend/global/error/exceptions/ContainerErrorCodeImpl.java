package com.sketch.brain.backend.global.error.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ContainerErrorCodeImpl implements ErrorCode{
    CONTAINER_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Requests is blocked. Cannot communicate with trainingContainers."),
    EXPERIMENT_START_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"Experiment Start failed."),
    EXPERIMENT_IS_NOT_READY(HttpStatus.INTERNAL_SERVER_ERROR,"Training Experiment is not ready, try it  later");

    private final HttpStatus httpStatus;
    private final String message;
}
