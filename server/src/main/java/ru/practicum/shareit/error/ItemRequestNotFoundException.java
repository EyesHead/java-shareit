package ru.practicum.shareit.error;

import jakarta.persistence.EntityNotFoundException;

public class ItemRequestNotFoundException extends EntityNotFoundException {
    public ItemRequestNotFoundException(long requestId) {
        super("Item request not found. ItemRequestId = " + requestId);
    }
}
