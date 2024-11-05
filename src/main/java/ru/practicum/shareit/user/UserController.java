package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserPostDto;
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
    public UserResponseDto createUser(@Valid @RequestBody UserPostDto userPostDto) {
        log.info("Received request to create new user with name : {}", userPostDto.getName());
        log.debug("User data: {}", userPostDto);

        UserResponseDto response = userService.createUser(userPostDto);

        log.info("User created successfully");
        return response;
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@Valid @RequestBody UserPatchDto userPatchDto,
                                      @PathVariable(name = "userId") long id) {
        log.info("Received request to update user with id: {}", id);
        log.debug("User update data: {}", userPatchDto);

        if (userPatchDto.getName() == null && userPatchDto.getEmail() == null) {
            log.warn("Nothing to update for user with id: {}", id);
            throw new UnsupportedOperationException("There is nothing to update for user. Id = " + id);
        }

        UserResponseDto response = userService.updateUser(userPatchDto, id);

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
    public void deleteUser(@PathVariable(name = "userId") long id) {
        log.info("Received request to delete user with id: {}", id);

        userService.removeUserById(id);

        log.info("User deleted successfully for id: {}", id);
    }
}
