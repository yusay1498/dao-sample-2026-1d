package com.example.event.api.presentation;

import com.example.event.api.domain.exception.EventCategoryNotFoundException;
import com.example.event.api.domain.exception.EventNotFoundException;
import com.example.event.api.domain.exception.IllegalPropertyException;
import com.example.event.api.domain.exception.VenueNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEventNotFoundException(EventNotFoundException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage())).build();
    }

    // venueId/eventCategoryIdはリクエストボディ内の参照値であるため、リソース自体の404ではなく400として扱う
    @ExceptionHandler({VenueNotFoundException.class, EventCategoryNotFoundException.class})
    public ResponseEntity<ProblemDetail> handleReferenceNotFoundException(RuntimeException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage())).build();
    }

    @ExceptionHandler(IllegalPropertyException.class)
    public ResponseEntity<ProblemDetail> handleIllegalPropertyException(IllegalPropertyException e) {
        logger.warn(e.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setProperty("name", e.getName());
        // 値が任意の入力（例: リクエスト本文全体）を含みうる場合はvalueを設定しないため、nullなら省略する
        if (e.getValue() != null) {
            problemDetail.setProperty("value", e.getValue());
        }
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage())).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warn(e.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("errors", e.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .toList());
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException e) {
        logger.warn(e.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("errors", e.getConstraintViolations().stream()
                .map(violation -> "%s: %s".formatted(violation.getPropertyPath(), violation.getMessage()))
                .toList());
        return ResponseEntity.of(problemDetail).build();
    }
}
