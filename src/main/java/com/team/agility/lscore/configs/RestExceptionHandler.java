package com.team.agility.lscore.configs;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.team.agility.lscore.dtos.BadRequestErrorResponse;
import com.team.agility.lscore.dtos.ConflictErrorResponse;
import com.team.agility.lscore.dtos.InvalidCredentialsErrorResponse;
import com.team.agility.lscore.dtos.NotFoundErrorResponse;
import com.team.agility.lscore.dtos.UnauthorizedErrorResponse;
import com.team.agility.lscore.exceptions.AuthorizationException;
import com.team.agility.lscore.exceptions.BadRequestException;
import com.team.agility.lscore.exceptions.DuplicateRecordException;
import com.team.agility.lscore.exceptions.InvalidCredentialsException;
import com.team.agility.lscore.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<?> handleAuthenticationException(
        AuthenticationException exception,
        HttpServletRequest request
    ) {
        UnauthorizedErrorResponse errors = new UnauthorizedErrorResponse();
        errors.setMethod(request.getMethod());
        errors.setPath(request.getRequestURI());
        errors.setStatus(HttpStatus.UNAUTHORIZED.value());
        errors.setError(exception.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<?> handleUsernameNotFoundException(
        UsernameNotFoundException exception,
        HttpServletRequest request
    ) {
        InvalidCredentialsErrorResponse errors = new InvalidCredentialsErrorResponse();
        errors.setMethod(request.getMethod());
        errors.setPath(request.getRequestURI());
        errors.setStatus(HttpStatus.UNAUTHORIZED.value());
        errors.setError(exception.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(DuplicateRecordException.class)
    protected ResponseEntity<?> handleConflictException(
        DuplicateRecordException exception,
        HttpServletRequest request
    ) {
        ConflictErrorResponse errors = new ConflictErrorResponse();
        errors.setMethod(request.getMethod());
        errors.setPath(request.getRequestURI());
        errors.setStatus(HttpStatus.CONFLICT.value());
        errors.setError(exception.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthorizationException.class)
    protected ResponseEntity<?> handleUnauthorizedAccessException(
        AuthorizationException exception,
        HttpServletRequest request
    ) {
        UnauthorizedErrorResponse errors = new UnauthorizedErrorResponse();
        errors.setMethod(request.getMethod());
        errors.setPath(request.getRequestURI());
        errors.setStatus(HttpStatus.UNAUTHORIZED.value());
        errors.setError(exception.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(TokenExpiredException.class)
    protected ResponseEntity<?> handleTokenExpiredException(
        TokenExpiredException exception,
        HttpServletRequest request
    ) {
        UnauthorizedErrorResponse errors = new UnauthorizedErrorResponse();
        errors.setMethod(request.getMethod());
        errors.setPath(request.getRequestURI());
        errors.setStatus(HttpStatus.UNAUTHORIZED.value());
        errors.setError(exception.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }



    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<?> handleResourceNotFoundException (
        ResourceNotFoundException exception,
        HttpServletRequest request
    ) {
        NotFoundErrorResponse errors = new  NotFoundErrorResponse();
        errors.setMethod(request.getMethod());
        errors.setPath(request.getRequestURI());
        errors.setStatus(HttpStatus.NOT_FOUND.value());
        errors.setError(exception.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    protected ResponseEntity<?> handleInvalidCredentialsException (
        InvalidCredentialsException exception,
        HttpServletRequest request
    ) {
        InvalidCredentialsErrorResponse errors = new InvalidCredentialsErrorResponse();
        errors.setMethod(request.getMethod());
        errors.setPath(request.getRequestURI());
        errors.setStatus(HttpStatus.FORBIDDEN.value());
        errors.setError(exception.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<?> handdleBadRequestException (
        BadRequestException exception,
        HttpServletRequest request
    ) {
        BadRequestErrorResponse errors = new BadRequestErrorResponse();
        errors.setMethod(request.getMethod());
        errors.setPath(request.getRequestURI());
        errors.setStatus(HttpStatus.BAD_REQUEST.value());
        errors.setError(exception.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
