package ru.practicum.shareit.error;

public class InvalidUpdateDataException extends RuntimeException {
    public InvalidUpdateDataException(String message) {
        super(message);
    }
}
