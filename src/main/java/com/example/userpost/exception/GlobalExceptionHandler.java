package com.example.userpost.exception;

import com.example.userpost.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException e) {
    return ResponseBuilder.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<?> handleBadRequest(BadRequestException e) {
    return ResponseBuilder.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGlobalException(Exception e) {
    return ResponseBuilder.internalServerError();
  }
}
