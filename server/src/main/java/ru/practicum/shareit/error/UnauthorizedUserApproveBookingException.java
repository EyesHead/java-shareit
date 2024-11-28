package ru.practicum.shareit.error;

public class UnauthorizedUserApproveBookingException extends RuntimeException {
    public UnauthorizedUserApproveBookingException(long bookingId, long userId) {
        super(String.format("User with id='%d' dont have enough rights for approve booking with id='%d'", userId, bookingId));
    }
}