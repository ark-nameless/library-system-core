package com.team.agility.lscore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super();
    }
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidCredentialsException(String message) {
        super(message);
    }
    public InvalidCredentialsException(Throwable cause) {
        super(cause);
    }
}