package ru.practicum.shareit.error;

public class UnavailableItemForBookingException extends RuntimeException {
    public UnavailableItemForBookingException(long itemId) {
        super("Item not unavailable for booking. ItemId = " + itemId);
    }
}
