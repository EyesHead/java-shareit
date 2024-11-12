package ru.practicum.shareit.error;

import jakarta.persistence.EntityNotFoundException;

public class ItemNotFoundException extends EntityNotFoundException {
    public ItemNotFoundException(long itemId) {
        super("Item not found. ItemId = " + itemId);
    }
}
