package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.exception.MessageNotFoundException;
import com.construction_worker_forum_back.model.dto.ExceptionDto;
import com.construction_worker_forum_back.validation.login.CustomFieldError;
import com.construction_worker_forum_back.validation.login.FieldErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ExceptionDto> conflictHandler(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionDto(e.getMessage()));
    }

    @ExceptionHandler(MessageNotFoundException.class)
    ResponseEntity<ExceptionDto> messageNotFoundHandler(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDto(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpStatus status) {
        FieldErrorResponse fieldErrorResponse = new FieldErrorResponse();

        List<CustomFieldError> fieldErrors = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            CustomFieldError fieldError = new CustomFieldError();
            fieldError.setField(((FieldError) error).getField());
            fieldError.setMessage(error.getDefaultMessage());
            fieldErrors.add(fieldError);
        });

        fieldErrorResponse.setFieldErrors(fieldErrors);
        return new ResponseEntity<>(fieldErrorResponse, status);
    }
}
