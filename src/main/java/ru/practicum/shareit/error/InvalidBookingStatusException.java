package ru.practicum.shareit.error;

public class InvalidBookingStatusException extends RuntimeException {
    public InvalidBookingStatusException(String str) {
        super(str);
    }
}
