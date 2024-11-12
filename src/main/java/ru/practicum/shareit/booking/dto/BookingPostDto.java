package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingPostDto {
    @NotNull
    Long itemId;

    @NotNull
    LocalDateTime start;

    @NotNull
    @Future(message = "End date must be in the future")
    LocalDateTime end;
}
