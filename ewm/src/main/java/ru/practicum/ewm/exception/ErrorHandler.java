package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.DateTimeMapper;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                e.getMessage(),
                DateTimeMapper.mapToString(ZonedDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.name(),
                "Bad request arguments.",
                e.getMessage(),
                DateTimeMapper.mapToString(ZonedDateTime.now())
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.name(),
                "Required parameter is missing.",
                e.getMessage(),
                DateTimeMapper.mapToString(ZonedDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                e.getMessage(),
                DateTimeMapper.mapToString(ZonedDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUniqueConstraintException(UniqueConstraintException e) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.name(),
                "Integrity constraint has been violated.",
                e.getMessage(),
                DateTimeMapper.mapToString(ZonedDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException e) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.name(),
                "Integrity constraint has been violated.",
                e.getMessage(),
                DateTimeMapper.mapToString(ZonedDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleConditionNotMetException(ConditionNotMetException e) {
        return new ErrorResponse(
                HttpStatus.FORBIDDEN.name(),
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                DateTimeMapper.mapToString(ZonedDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleDataAccessException(DataAccessException e) {
        return new ErrorResponse(
                HttpStatus.FORBIDDEN.name(),
                "Access denied.",
                e.getMessage(),
                DateTimeMapper.mapToString(ZonedDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Internal server error occurred",
                e.getLocalizedMessage(),
                DateTimeMapper.mapToString(ZonedDateTime.now())
        );
    }
}
