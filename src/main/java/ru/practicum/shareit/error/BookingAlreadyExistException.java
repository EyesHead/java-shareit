package ru.practicum.shareit.error;

public class BookingAlreadyExistException extends RuntimeException {
    public BookingAlreadyExistException(long bookerId, long itemId) {
        super("Booking already exist. BookerId = " + bookerId + ". ItemId = " + itemId);
    }
}
