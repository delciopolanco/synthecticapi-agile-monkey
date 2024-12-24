package com.agilemonkeys.syntheticapi.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundExceptions extends RuntimeException {
    public CustomerNotFoundExceptions(String message) {
        super(message);
    }
}
