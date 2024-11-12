package ru.practicum.shareit.error;

import java.time.LocalDateTime;

public class InvalidBookingDateException extends RuntimeException {
    public InvalidBookingDateException(LocalDateTime start, LocalDateTime end) {
        super("Invalid booking dates. start=" + start + ", end=" + end);
    }
}
