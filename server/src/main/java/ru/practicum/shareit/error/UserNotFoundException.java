package ru.practicum.shareit.error;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(long userId) {
        super("User not found. Id = " + userId);
    }
}