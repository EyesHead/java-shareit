package ru.practicum.shareit.item.dto;

import lombok.*;

@Value
@RequiredArgsConstructor
public class ItemResponseDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
}