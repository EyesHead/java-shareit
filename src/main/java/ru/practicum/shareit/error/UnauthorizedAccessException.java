package ru.practicum.shareit.error;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("User does not have access to this booking.");
    }
}
