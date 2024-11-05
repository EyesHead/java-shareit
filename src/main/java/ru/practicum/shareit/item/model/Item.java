package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    Long request;
}
