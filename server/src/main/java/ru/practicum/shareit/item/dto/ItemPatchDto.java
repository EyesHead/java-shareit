package ru.practicum.shareit.item.dto;

import lombok.Value;

@Value
public class ItemPatchDto {
    String name;
    String description;
    Boolean available;
}