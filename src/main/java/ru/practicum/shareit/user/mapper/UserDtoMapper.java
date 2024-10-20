package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserPostDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    User toUser(UserPostDto userCreate);

    User toUser(UserPatchDto userUpdate);

    UserResponseDto toDto(User user);
}
