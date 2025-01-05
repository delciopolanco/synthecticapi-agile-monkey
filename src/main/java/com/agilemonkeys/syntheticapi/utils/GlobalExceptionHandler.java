package com.agilemonkeys.syntheticapi.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({ CustomerUniqueException.class })
  public ResponseEntity<Object> handleCustomerExistException(CustomerUniqueException exception) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(exception.getMessage());
  }

  @ExceptionHandler({ CustomerNotFoundExceptions.class })
  public ResponseEntity<Object> handleCustomerNotFoundException(CustomerNotFoundExceptions exception) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(exception.getMessage());
  }

  @ExceptionHandler({ UserNotFoundExceptions.class })
  public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundExceptions exception) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(exception.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.put(error.getField(), error.getDefaultMessage());
    });

    return ResponseEntity.badRequest().body(errors);
  }
}
