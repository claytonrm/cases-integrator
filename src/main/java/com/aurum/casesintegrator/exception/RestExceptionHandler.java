package com.aurum.casesintegrator.exception;

import java.util.Set;
import java.util.stream.Collectors;

import javax.management.InstanceAlreadyExistsException;
import javax.validation.ConstraintViolationException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<Object> handleIllegalStateException(final IllegalStateException e) {
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, Set.of(e.getMessage())));
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException e, final HttpHeaders headers,
                                                               final HttpStatus status, final WebRequest request) {
        final Set<String> errors = e.getBindingResult().getFieldErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.toSet());
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, errors));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(InstanceAlreadyExistsException.class)
    public ResponseEntity<Object> handleInstanceAlreadyExistsException(final InstanceAlreadyExistsException e) {
        return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, Set.of(e.getMessage())));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException e) {
        final Set<String> errors = e.getConstraintViolations().stream().map(constraintViolation -> constraintViolation.getMessageTemplate()).collect(Collectors.toSet());
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, errors));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(final IllegalArgumentException e) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, Set.of(e.getMessage())));
    }

    private ResponseEntity<Object> buildResponseEntity(final ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
