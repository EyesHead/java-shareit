package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.Collection;

public interface BookingService {
    BookingResponseDto createBooking(BookingPostDto bookingForCreate, long bookerId);

    BookingResponseDto approveBooking(long bookingId, long ownerId, boolean isApproved);

    Collection<BookingResponseDto> getAllBookingsByBookerIdAndBookingStatus(long bookerId, String state);

    Collection<BookingResponseDto> getByOwnerIdAndBookingStatus(long ownerId, String state);

    BookingResponseDto getBookingByIdAndAuthorizedUserId(long bookingId, long userId);
}
