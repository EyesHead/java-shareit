package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class ItemPatchDto {
    String name;
    String description;
    Boolean available;
}