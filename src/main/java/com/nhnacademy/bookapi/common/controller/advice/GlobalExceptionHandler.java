package com.nhnacademy.bookapi.common.controller.advice;

import com.nhnacademy.bookapi.common.exception.CustomHttpException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomHttpException.class)
    public ResponseEntity<ErrorMessage> handleException(CustomHttpException e, HttpServletRequest request) {

        int statusCode = e.getCustomHttpStatus().getCode();
        String reasonPhrase = e.getCustomHttpStatus().name();

        ErrorMessage errorMessage = new ErrorMessage(
                statusCode,
                reasonPhrase,
                request.getRequestURI(),
                e.getMessage()
        );
        return ResponseEntity.status(statusCode).body(errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception e, HttpServletRequest request) {
        CustomHttpException.CustomHttpStatus status = CustomHttpException.CustomHttpStatus.INTERNAL_SERVER_ERROR;
        ErrorMessage errorMessage = new ErrorMessage(
                status.getCode(),
                status.name(),
                request.getRequestURI(),
                e.getMessage()
        );
        return ResponseEntity.status(status.getCode()).body(errorMessage);
    }
}
