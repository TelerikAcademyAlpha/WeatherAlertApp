package com.backend.weatheralertapp.exceptions;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
    public class ApiExceptionHandler{

        private static final Logger log =
                LoggerFactory.getLogger(ApiExceptionHandler.class);

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidationErrors(
                MethodArgumentNotValidException exception,
                HttpServletRequest request
        ) {
            Map<String, String> validationErrors = new LinkedHashMap<>();

            exception.getBindingResult()
                    .getFieldErrors()
                    .forEach(fieldError ->
                            validationErrors.putIfAbsent(
                                    fieldError.getField(),
                                    fieldError.getDefaultMessage()
                            )
                    );

            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed",
                    "One or more request fields are invalid",
                    request.getRequestURI(),
                    validationErrors
            );
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
                ConstraintViolationException exception,
                HttpServletRequest request
        ) {
            Map<String, String> validationErrors = new LinkedHashMap<>();

            exception.getConstraintViolations().forEach(violation ->
                    validationErrors.put(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    )
            );

            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed",
                    "One or more values are invalid",
                    request.getRequestURI(),
                    validationErrors
            );
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiErrorResponse> handleInvalidRequestBody(
                HttpMessageNotReadableException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    "Invalid request body",
                    "The request body is missing, malformed, or contains an invalid value",
                    request.getRequestURI(),
                    Map.of()
            );
        }

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ApiErrorResponse> handleResponseStatus(
                ResponseStatusException exception,
                HttpServletRequest request
        ) {
            HttpStatusCode status = exception.getStatusCode();

            String message = exception.getReason() != null
                    ? exception.getReason()
                    : "The request could not be completed";

            return buildResponse(
                    status,
                    "Request failed",
                    message,
                    request.getRequestURI(),
                    Map.of()
            );
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiErrorResponse> handleAccessDenied(
                AccessDeniedException exception,
                HttpServletRequest request
        ) {
            return buildResponse(
                    HttpStatus.FORBIDDEN,
                    "Forbidden",
                    "You do not have permission to perform this action",
                    request.getRequestURI(),
                    Map.of()
            );
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
                Exception exception,
                HttpServletRequest request
        ) {
            log.error("Unexpected error for request {}", request.getRequestURI(), exception);

            return buildResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal server error",
                    "An unexpected error occurred",
                    request.getRequestURI(),
                    Map.of()
            );
        }

        private ResponseEntity<ApiErrorResponse> buildResponse(
                HttpStatusCode status,
                String error,
                String message,
                String path,
                Map<String, String> validationErrors
        ) {
            ApiErrorResponse body = new ApiErrorResponse(
                    Instant.now(),
                    status.value(),
                    error,
                    message,
                    path,
                    validationErrors
            );

            return ResponseEntity.status(status).body(body);
        }
}
