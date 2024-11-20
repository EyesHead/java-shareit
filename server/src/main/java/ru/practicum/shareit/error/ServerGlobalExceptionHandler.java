package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ServerGlobalExceptionHandler {
    @ExceptionHandler({
            UnavailableItemForBookingException.class,
            InvalidBookingStatusForApprovingException.class,
            UnauthorizedCommentCreateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(RuntimeException ex) {
        return getMappedExceptionResponseMessage(ex);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            BookingNotFoundException.class,
            ItemNotFoundException.class,
            ItemRequestNotFoundException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(RuntimeException ex) {
        return getMappedExceptionResponseMessage(ex);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailDuplicateException(RuntimeException ex) {
        return getMappedExceptionResponseMessage(ex);
    }

    @ExceptionHandler({
            UnauthorizedUserApproveBookingException.class,
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleUnauthorizedBookingApprovalException(RuntimeException ex) {
        return getMappedExceptionResponseMessage(ex);
    }

    private static Map<String, String> getMappedExceptionResponseMessage(RuntimeException ex) {
        log.warn("Exception: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return response;
    }
}