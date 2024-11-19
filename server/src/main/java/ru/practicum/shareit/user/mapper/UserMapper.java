package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequestDto userCreate);

    UserResponseDto toDto(User user);
}
