package com.sketch.brain.backend.global.error.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ContainerErrorCodeImpl implements ErrorCode{
    CONTAINER_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"Requests is blocked. Cannot communicate with trainingContainers.");

    private final HttpStatus httpStatus;
    private final String message;
}
