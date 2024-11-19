package ru.practicum.shareit.error;

public class UnauthorizedUserGetBookingsException extends RuntimeException {
    public UnauthorizedUserGetBookingsException(long userId) {
        super(String.format(
                "User with id = '%d' doesnt have access to retrieve bookings. Should be item renter or owner",
                userId)
        );
    }
}
