package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class UserPatchDto {
    String name;
    @Email
    String email;
}
