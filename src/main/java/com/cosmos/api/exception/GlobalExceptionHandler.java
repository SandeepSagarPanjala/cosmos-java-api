package com.cosmos.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Maps exceptions to HTTP responses (JD 1.3 status, JD 1.5 body shape, JD 1.7 command errors).
 * <p>
 * Uses {@link ProblemDetail} for RFC 7807-style “problem” JSON: {@code type}, {@code title}, {@code status},
 * {@code detail}, {@code instance}, plus extension members where useful.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    /**
     * 400 — e.g. {@code @NotBlank} on {@code @RequestHeader} when controller is {@code @Validated}.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        Map<String, String> violations = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> {
            String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "parameter";
            violations.put(path, v.getMessage());
        });
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "One or more request parameters or headers are invalid.");
        problem.setTitle("Constraint violation");
        problem.setType(ProblemTypeUri.VALIDATION_FAILED);
        problem.setInstance(URI.create(Objects.toString(request.getRequestURI(), "/")));
        problem.setProperty("violations", violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(TransferNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleTransferNotFound(TransferNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Transfer not found");
        problem.setType(ProblemTypeUri.TRANSFER_NOT_FOUND);
        problem.setInstance(URI.create(Objects.toString(request.getRequestURI(), "/")));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ProblemDetail> handleIdempotencyConflict(
            IdempotencyConflictException ex,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Idempotency key conflict");
        problem.setType(ProblemTypeUri.IDEMPOTENCY_CONFLICT);
        problem.setInstance(URI.create(Objects.toString(request.getRequestURI(), "/")));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(InvalidTransferRequestException.class)
    public ResponseEntity<ProblemDetail> handleInvalidTransfer(
            InvalidTransferRequestException ex,
            HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid transfer");
        problem.setType(ProblemTypeUri.INVALID_TRANSFER);
        problem.setInstance(URI.create(Objects.toString(request.getRequestURI(), "/")));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    /**
     * Fallback for uncaught <b>runtime</b> failures (e.g. {@link ArithmeticException}, {@link NullPointerException}).
     * Uses {@link RuntimeException} (not {@link Exception}) so servlet/framework checked errors keep Spring’s defaults.
     * <p>
     * Logs the real stack trace server-side; clients get a generic message (avoid leaking implementation details).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(RuntimeException ex, HttpServletRequest request) {
        log.error("Unhandled exception for {}", request.getRequestURI(), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
        problem.setTitle("Internal server error");
        problem.setType(ProblemTypeUri.INTERNAL_ERROR);
        problem.setInstance(URI.create(Objects.toString(request.getRequestURI(), "/")));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
