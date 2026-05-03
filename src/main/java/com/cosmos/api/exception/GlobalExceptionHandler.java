package com.cosmos.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Maps exceptions to HTTP responses (JD 1.3 status, JD 1.5 body shape).
 * <p>
 * Uses {@link ProblemDetail} for RFC 7807-style “problem” JSON: {@code type}, {@code title}, {@code status},
 * {@code detail}, {@code instance}, plus extension members where useful.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404 — resource not found.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("User not found");
        problem.setType(ProblemTypeUri.USER_NOT_FOUND);
        problem.setInstance(URI.create(Objects.toString(request.getRequestURI(), "/")));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    /**
     * 400 — Bean Validation failed on a {@code @Valid} request body.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            if (error == null) {
                continue;
            }
            if (error instanceof FieldError fieldError) {
                String field = fieldError.getField() != null ? fieldError.getField() : "field";
                String message = fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid";
                fieldErrors.put(field, message);
            } else {
                String name = error.getObjectName() != null ? error.getObjectName() : "request";
                String message = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid";
                fieldErrors.put(name, message);
            }
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "One or more fields failed validation.");
        problem.setTitle("Validation failed");
        problem.setType(ProblemTypeUri.VALIDATION_FAILED);
        problem.setInstance(URI.create(Objects.toString(request.getRequestURI(), "/")));
        problem.setProperty("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }
}
