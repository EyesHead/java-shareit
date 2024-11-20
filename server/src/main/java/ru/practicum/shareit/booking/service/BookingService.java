package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingStatus;

import java.util.Collection;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingForCreate, long bookerId);

    BookingResponseDto approveBooking(long bookingId, long ownerId, boolean isApproved);

    Collection<BookingResponseDto> getAllByRenterIdAndStatus(long bookerId, BookingStatus status);

    Collection<BookingResponseDto> getAllByItemOwnerIdAndStatus(long bookerId, BookingStatus status);

    BookingResponseDto getBookingByIdAndUserId(long bookingId, long userId);
}