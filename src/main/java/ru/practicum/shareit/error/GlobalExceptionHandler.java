package ru.practicum.shareit.error;

import jakarta.persistence.EntityNotFoundException;
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
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Constraint violation:\n{}", errors);
        return errors;
    }

    @ExceptionHandler({UnavailableItemBookingException.class, InvalidBookingDateException.class, InvalidBookingStatusException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidDataRequestException(RuntimeException ex) {
        return getMappedExceptionResponseMessage(ex);
    }

    @ExceptionHandler({EntityNotFoundException.class, UnsupportedOperationException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(RuntimeException ex) {
        return getMappedExceptionResponseMessage(ex);
    }

    @ExceptionHandler({EmailAlreadyExistsException.class, BookingAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailDuplicateException(RuntimeException ex) {
        return getMappedExceptionResponseMessage(ex);
    }

    @ExceptionHandler(UnauthorizedBookingApprovalException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleUnauthorizedBookingApprovalException(UnauthorizedBookingApprovalException ex) {
        return getMappedExceptionResponseMessage(ex);
    }

    private static Map<String, String> getMappedExceptionResponseMessage(RuntimeException ex) {
        log.warn("Exception: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return response;
    }
}