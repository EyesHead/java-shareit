package ru.practicum.shareit.error;

import jakarta.persistence.EntityNotFoundException;

public class BookingNotFoundException extends EntityNotFoundException {
    public BookingNotFoundException(long bookingId) {
        super("Booking not found by bookingId = " + bookingId);
    }

    public BookingNotFoundException(long itemId, long renterId) {
        super("Booking not found by itemId = " + itemId + "and bookerId = " + renterId);
    }
}