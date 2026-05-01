package org.test.parking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.test.parking.api.response.ErrorResponse;
import org.test.parking.exception.domain.DomainException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(NotFoundException e) {
        return new ErrorResponse("NOT_FOUND", e);
    }

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflict(DomainException e) {
        return new ErrorResponse("CONFLICT", e);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse bad(BadRequestException e) {
        return new ErrorResponse("BAD_REQUEST", e);
    }

    //TODO: Handle massive MethodArgumentNotValidException message
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationError(MethodArgumentNotValidException e) {
        return new ErrorResponse("BAD_REQUEST", e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse other(Exception e) {
        return new ErrorResponse("INTERNAL_ERROR", e);
    }
}
