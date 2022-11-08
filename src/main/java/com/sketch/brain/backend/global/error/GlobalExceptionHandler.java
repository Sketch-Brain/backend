package com.sketch.brain.backend.global.error;

import com.sketch.brain.backend.global.error.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// 차후 Exception Handling 보강 예정.
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Http Message 를 Converte 하거나, Binding error 가 발생할 경우.
     * @RequestBody binding 에러도 여기.
     * @param e MethodArgumentNotValidException
     * @return ResponseEntity<>(ErrorResponse,HttpStatus)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentException(MethodArgumentNotValidException e){
        log.error("Method arguments are not valid", e);
        final ErrorResponse response = new ErrorResponse(CommonErrorCodeImpl.INVALID_PARAMETER,e.getMessage());
        return new ResponseEntity<>(response,CommonErrorCodeImpl.INVALID_PARAMETER.getHttpStatus());
    }

    /**
     * Argument Type 이 서로 일치하지 않는 경우에 대한 Exception handling.
     * @param e : MethodArgumentTypeMismatchException
     * @return ResponseEntity<>(ErrorResponse,HttpStatus)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgsTypeMismatchException(MethodArgumentTypeMismatchException e){
        log.error("Method argument type mismatch", e);
        final ErrorResponse errorResponse = ErrorResponse.create(e);
        return new ResponseEntity<>(errorResponse,CommonErrorCodeImpl.INVALID_ARGUMENT_TYPE.getHttpStatus());
    }

    /**
     * 허가되지 않는 Method Type 으로 요청한 경우에 대한 Exception Handling.
     * @param e HttpRequestMethodNotSupportedException
     * @return ResponseEntity<>(ErrorResponse,HttpStatus)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleRequestHttpMethodNotSupportException(HttpRequestMethodNotSupportedException e){
        log.error("Request http methods are not allowed.", e);
        final ErrorResponse errorResponse = new ErrorResponse(CommonErrorCodeImpl.METHOD_NOT_ALLOWED,CommonErrorCodeImpl.METHOD_NOT_ALLOWED.getMessage());
        return new ResponseEntity<>(errorResponse,CommonErrorCodeImpl.METHOD_NOT_ALLOWED.getHttpStatus());
    }

    /**
     * Training 을 할 때 생기는 Exception Handling.
     */
    @ExceptionHandler(TrainingExceptions.class)
    protected ResponseEntity<ErrorResponse> handleUnknownLayerTraining(TrainingExceptions e){
        log.error("Unknown Training Layer detected.", e);
        final ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(),e.getErrorCode().getMessage());
        return new ResponseEntity<>(errorResponse,e.getErrorCode().getHttpStatus());
    }

    /**
     * Validation Error Handler
     */
    @ExceptionHandler(ValidationExceptions.class)
    protected ResponseEntity<ErrorResponse> handleValidationExceptions(ValidationExceptions e){
        log.error("Validation Error acc.");
        final ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(),e.getErrorCode().getMessage(),e.getArgumentError());
        return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
    }

    /**
     * Container(Kubernetes) Error Handler.
     * @param e Kubernetes related errors.
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(ContainerExceptions.class)
    protected ResponseEntity<ErrorResponse> handleContainerExceptions(ContainerExceptions e){
        log.error("Container Exceptions acc.");
        final ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(),e.getErrorCode().getMessage(),e.getArgumentError());
        return new ResponseEntity<>(errorResponse,e.getErrorCode().getHttpStatus());
    }
}
