package com.wara.usermanagement.usermanagement.handler;

import com.wara.usermanagement.usermanagement.dto.response.ErrorResponse;
import com.wara.usermanagement.usermanagement.exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(HttpServletRequest request, ApplicationException e) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(HttpServletRequest request, Throwable e) {
        FieldError fieldError = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError();
        String message = fieldError != null
                ? "field '" + fieldError.getField() + "' " + fieldError.getDefaultMessage()
                : "validation failed";
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handlerException(HttpServletRequest request, Throwable e) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
