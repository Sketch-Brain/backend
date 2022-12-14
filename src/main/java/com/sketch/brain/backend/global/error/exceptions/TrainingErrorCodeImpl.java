package com.sketch.brain.backend.global.error.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TrainingErrorCodeImpl implements ErrorCode{

    UNKNOWN_LAYER_DETECTED(HttpStatus.BAD_REQUEST,"Unknown layer detected."),
    NO_EXPERIMENT_DETECTED(HttpStatus.NOT_FOUND,"There is no experiments.");

    private final HttpStatus httpStatus;
    private final String message;
}
