package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Value
@RequiredArgsConstructor
public class UserPostDto {
    @NotBlank
    String name;
    @NotBlank
    @Email
    String email;
}