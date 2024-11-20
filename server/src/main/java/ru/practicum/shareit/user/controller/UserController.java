package ru.practicum.shareit.user.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

@RequestMapping(path = "/users")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        log.info("Received request to create new user with name : {}", userRequestDto.getName());
        UserResponseDto response = userService.createUser(userRequestDto);

        log.info("User created successfully");
        return response;
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@RequestBody UserRequestDto userRequestDto,
                                      @PathVariable(name = "userId") long id) {
        UserResponseDto response = userService.updateUser(userRequestDto, id);

        log.info("User updated successfully for id: {}", id);
        return response;
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable(name = "userId") long id) {
        log.info("Received request to get user with id: {}", id);

        UserResponseDto response = userService.getUserById(id);

        log.info("Returning user details for id: {}", id);
        return response;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable(name = "userId") long userId) {
        log.info("Received request to delete user with userId: {}", userId);

        userService.removeUserById(userId);

        log.info("User deleted successfully for userId: {}", userId);
    }
}