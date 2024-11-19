package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class UserResponseDto {
    Long id;
    String name;
    String email;
}
