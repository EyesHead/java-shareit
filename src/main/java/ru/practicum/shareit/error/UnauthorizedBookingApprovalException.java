package ru.practicum.shareit.error;

public class UnauthorizedBookingApprovalException extends RuntimeException {
    public UnauthorizedBookingApprovalException(long bookingId, long userId) {
        super(String.format("User with ID %d is not the owner of the item for booking with ID %d", userId, bookingId));
    }
}