package ru.practicum.shareit.error;

public class UnavailableItemBookingException extends RuntimeException {
    public UnavailableItemBookingException(long itemId) {
        super("Item not unavailable for booking. ItemId = " + itemId);
    }
}
