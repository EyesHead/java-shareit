package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    Long id;
    String name;
    String email;
}