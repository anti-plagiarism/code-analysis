package com.vk.codeanalysis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExceptions(Exception ex)
    {
        log.error("Error during processing files: ", ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder(ex, INTERNAL_SERVER_ERROR, ex.getMessage()).build()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentExceptions(Exception ex)
    {
        log.error("Error during file processing: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder(ex, BAD_REQUEST, ex.getMessage()).build()
        );
    }
}
