package ru.practicum.shareit.user.dto;

import lombok.*;

@Value
@RequiredArgsConstructor
public class UserResponseDto {
    Long id;
    String name;
    String email;
}
