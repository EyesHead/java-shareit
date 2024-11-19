package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class UserRequestDto {
    String name;
    String email;
}