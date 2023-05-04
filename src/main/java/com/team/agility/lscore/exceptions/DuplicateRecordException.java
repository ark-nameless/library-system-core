package com.team.agility.lscore.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateRecordException extends RuntimeException {
    public DuplicateRecordException() {
        super();
    }
    public DuplicateRecordException(String message, Throwable cause) {
        super(message, cause);
    }
    public DuplicateRecordException(String message) {
        super(message);
    }
    public DuplicateRecordException(Throwable cause) {
        super(cause);
    }
}