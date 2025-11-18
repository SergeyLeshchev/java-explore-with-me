package ru.practicum.ewm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse extends RuntimeException {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}
