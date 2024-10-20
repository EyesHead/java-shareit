package ru.practicum.shareit.exception;

public class ItemOfUserNotFoundException extends RuntimeException {
    public ItemOfUserNotFoundException(Long itemId, Long userId) {
        super("Item '" + itemId + "' of user with id '" + userId + "'not found");
    }
}
