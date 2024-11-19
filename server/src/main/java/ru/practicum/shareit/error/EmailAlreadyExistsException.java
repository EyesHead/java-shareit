package ru.practicum.shareit.error;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("User with email = ' " + email + " already exist");
    }
}