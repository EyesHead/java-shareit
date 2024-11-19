package ru.practicum.shareit.error;

import ru.practicum.shareit.booking.dto.BookingStatus;

public class InvalidBookingStatusForApprovingException extends RuntimeException {
    public InvalidBookingStatusForApprovingException(BookingStatus bookingStatus) {
        super(String.format(
                "Booking status '%s' should be equals to %s for approving booking", bookingStatus, BookingStatus.WAITING)
        );
    }
}