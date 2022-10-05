package com.sketch.brain.backend.global.error.exceptions;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    /**
     * get HttpStatus Code.
     * @return HttpStatus Code.
     */
    HttpStatus getHttpStatus();

    String name();

    /**
     * getError Messages string.
     * @return Error Message String.
     */
    String getMessage();
}
