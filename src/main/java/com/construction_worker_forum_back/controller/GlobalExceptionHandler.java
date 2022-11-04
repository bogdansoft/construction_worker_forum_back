package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.exception.MessageNotFoundException;
import com.construction_worker_forum_back.model.dto.ExceptionDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
