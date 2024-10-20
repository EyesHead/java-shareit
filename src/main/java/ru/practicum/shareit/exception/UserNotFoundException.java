package ru.practicum.shareit.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long userId) {
        super("User not found. Id = " + userId);
    }
}