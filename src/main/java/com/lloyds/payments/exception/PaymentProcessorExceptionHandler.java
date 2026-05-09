package com.lloyds.payments.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class PaymentProcessorExceptionHandler {


    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(BaseException ex) {

        ApiError error = new ApiError();
        error.setMessage(ex.getMessage());
        error.setErrorCode(ex.getErrorCode());
        error.setStatus(ex.getStatus());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {

        ApiError error = new ApiError();
        error.setMessage(ErrorCode.INTERNAL_ERROR.getDefaultMessage());
        error.setErrorCode(ErrorCode.INTERNAL_ERROR.getCode());
        error.setStatus(ErrorCode.INTERNAL_ERROR.getStatus());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(500).body(error);
    }

}
