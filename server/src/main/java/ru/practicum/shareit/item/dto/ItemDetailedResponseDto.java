package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDetailedResponseDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Collection<CommentResponseDto> comments;
    BookingResponseDto lastBooking;
    BookingResponseDto nextBooking;
}