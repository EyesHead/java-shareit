package ru.practicum.shareit.error;

import jakarta.persistence.EntityNotFoundException;

public class ItemOwnerNotFoundException extends EntityNotFoundException {
    public ItemOwnerNotFoundException(Long itemId, Long userId) {
        super("Item '" + itemId + "' of owner with userId '" + userId + "'not found");
    }
}
