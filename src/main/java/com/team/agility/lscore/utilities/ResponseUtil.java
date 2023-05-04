package com.team.agility.lscore.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.agility.lscore.dtos.Errors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

@UtilityClass
public class ResponseUtil {

    public void writeErrorResponse(
            HttpStatus httpStatus,
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws IOException {

        response.setStatus(httpStatus.value());

        Errors errors = new Errors();
        errors.setMethod(request.getMethod());
        errors.setStatus(httpStatus.value());
        errors.setError(message);
        errors.setPath(request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), errors);
    }
}