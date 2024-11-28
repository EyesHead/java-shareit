package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.InvalidUpdateDataException;
import ru.practicum.shareit.user.dto.UserPatchRequestDto;
import ru.practicum.shareit.user.dto.UserPostRequestDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@Profile("gateway")
public class UserGatewayController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserPostRequestDto userPostDto) {
        log.info("Received request to create new user with data : {}", userPostDto);
        return userClient.createUser(userPostDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserPatchRequestDto userPatchDto,
                                             @Positive @PathVariable(name = "userId") long userId) {
        if (userPatchDto.getName() == null && userPatchDto.getEmail() == null) {
            throw new InvalidUpdateDataException("There is nothing to update for user. Id = " + userId);
        }

        return userClient.updateUser(userId, userPatchDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@Positive @PathVariable(name = "userId") long userId) {
        log.info("Received request to get user with id='{}'", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable(name = "userId") long userId) {
        log.info("Received request to delete user with userId='{}'", userId);
        userClient.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}