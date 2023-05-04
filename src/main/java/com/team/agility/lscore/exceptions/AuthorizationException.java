package com.team.agility.lscore.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthorizationException extends RuntimeException {
    public AuthorizationException() {
        super();
    }
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
    public AuthorizationException(String message) {
        super(message);
    }
    public AuthorizationException(Throwable cause) {
        super(cause);
    }
}