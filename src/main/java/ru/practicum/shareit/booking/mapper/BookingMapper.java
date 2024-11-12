package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking toBooking(BookingPostDto postDto);

    BookingResponseDto toResponse(Booking booking);

    Collection<BookingResponseDto> toResponseList(List<Booking> bookings);
}
