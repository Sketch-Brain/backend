package com.sketch.brain.backend.global.error;

import com.sketch.brain.backend.global.error.exceptions.CommonErrorCodeImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// 차후 Exception Handling 보강 예정.
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentException(MethodArgumentNotValidException e){
        log.error("Method arguments are not valid", e);
        final ErrorResponse response =
                ErrorResponse.builder().code(CommonErrorCodeImpl.INVALID_PARAMETER.getHttpStatus())
                        .message(e.getMessage()).build();
        return new ResponseEntity<>(response,CommonErrorCodeImpl.INVALID_PARAMETER.getHttpStatus());
    }
}
