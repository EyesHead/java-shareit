package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingFindStatus;

import java.util.Collection;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingForCreate, long bookerId);

    BookingResponseDto approveBooking(long bookingId, long ownerId, boolean isApproved);

    Collection<BookingResponseDto> getAllByRenterIdAndFindStatus(long bookerId, BookingFindStatus status);

    Collection<BookingResponseDto> getAllByOwnerIdAndFindStatus(long bookerId, BookingFindStatus status);

    BookingResponseDto getBookingByIdAndUserId(long bookingId, long userId);
}