package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void shouldMapBookingRequestDtoToBooking() {
        // Arrange
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L, // itemId
                LocalDateTime.of(2024, 11, 26, 12, 0, 0), // start
                LocalDateTime.of(2024, 11, 26, 14, 0, 0) // end
        );

        // Act
        Booking booking = bookingMapper.toBooking(bookingRequestDto);

        // Assert
        assertEquals(bookingRequestDto.getStart(), booking.getStart());
        assertEquals(bookingRequestDto.getEnd(), booking.getEnd());
        // Дополнительная проверка по полям, например, по статусу
    }

    @Test
    void shouldMapBookingToBookingResponseDto() {
        // Arrange
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 11, 26, 12, 0, 0))
                .end(LocalDateTime.of(2024, 11, 26, 14, 0, 0))
                .status(BookingStatus.WAITING) // Пример статуса
                .build();

        UserResponseDto userResponseDto = new UserResponseDto(1L, "John", "john@example.com");
        ItemResponseDto itemResponseDto = new ItemResponseDto(1L, "ItemName", "Some item description", true);

        booking.setBooker(new User());
        booking.setItem(new Item());

        BookingResponseDto expectedResponseDto = new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                userResponseDto,
                itemResponseDto
        );

        // Act
        BookingResponseDto actualResponseDto = bookingMapper.toResponse(booking);

        // Assert
        assertEquals(expectedResponseDto.getId(), actualResponseDto.getId());
        assertEquals(expectedResponseDto.getStart(), actualResponseDto.getStart());
        assertEquals(expectedResponseDto.getEnd(), actualResponseDto.getEnd());
        assertEquals(expectedResponseDto.getStatus(), actualResponseDto.getStatus());
    }

    @Test
    void shouldMapBookingListToBookingResponseDtoList() {
        // Arrange
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 11, 26, 12, 0, 0))
                .end(LocalDateTime.of(2024, 11, 26, 14, 0, 0))
                .status(BookingStatus.REJECTED)
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.of(2024, 11, 27, 12, 0, 0))
                .end(LocalDateTime.of(2024, 11, 27, 14, 0, 0))
                .status(BookingStatus.APPROVED)
                .build();

        List<Booking> bookings = List.of(booking1, booking2);

        // Act
        Collection<BookingResponseDto> responseDtos = bookingMapper.toResponseList(bookings);

        // Assert
        assertEquals(2, responseDtos.size());
        BookingResponseDto responseDto1 = responseDtos.stream().filter(dto -> dto.getId().equals(1L)).findFirst().orElseThrow();
        assertEquals(1L, responseDto1.getId());
        assertEquals(BookingStatus.REJECTED, responseDto1.getStatus());
    }
}
