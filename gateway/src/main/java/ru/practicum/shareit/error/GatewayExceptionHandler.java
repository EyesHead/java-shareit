package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GatewayExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put("error", error.getField() + " violation :" + error.getDefaultMessage())
        );
        log.warn("Constraint violation:\n{}", errors.values());
        return errors;
    }

    @ExceptionHandler({
            InvalidBookingStatusException.class,
            InvalidBookingDateException.class,
            InvalidUpdateDataException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(RuntimeException ex) {
        return getMappedExceptionResponseMessage(ex);
    }

    private static Map<String, String> getMappedExceptionResponseMessage(RuntimeException ex) {
        log.warn("Exception: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return response;
    }
}
