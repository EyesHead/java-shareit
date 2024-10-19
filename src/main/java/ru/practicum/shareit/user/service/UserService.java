package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserPostDto;

public interface UserService {
    UserResponseDto createUser(UserPostDto userPostDto);

    UserResponseDto updateUser(UserPatchDto userPatchDto, long id);

    UserResponseDto getUserById(long id);

    void removeUserById(long id);
}
