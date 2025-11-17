package com.b2z.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        String violations = e.getConstraintViolations().stream()
                .map(v -> {
                    String path = v.getPropertyPath().toString();
                    String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                    if (field.isEmpty()) field = "Champ";
                    String capitalized = field.substring(0, 1).toUpperCase() + field.substring(1);
                    String message = v.getMessage();
                    return capitalized + ": " + message;
                })
                .collect(java.util.stream.Collectors.joining("; "));
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(java.util.Map.of("violations", violations))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}