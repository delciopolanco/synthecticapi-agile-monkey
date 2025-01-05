package com.agilemonkeys.syntheticapi.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CustomerUniqueException extends RuntimeException {
    public CustomerUniqueException(String message) {
        super(message);
    }
}
