package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.error.EmailAlreadyExistsException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UpdateUserIntegrationTest {
    @Autowired
    private UserServiceImpl userService;

    @Test
    void updateUser_whenUpdateEmailWhichAlreadyExists_shouldThrowEmailAlreadyExistsException() {
        UserResponseDto user1 = userService.createUser(new UserRequestDto("ASDFGH", "duplicate@example.com"));
        UserResponseDto user2 = userService.createUser(new UserRequestDto("ASFMQOLMF", "something@example.com"));

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.updateUser(
                        new UserRequestDto("SOMENAME", user2.getEmail()),
                        user1.getId()));
        assertTrue(exception.getMessage().contains(user2.getEmail()));
    }

    @Test
    void updateUser_whenUpdateNameAndEmail_shouldReturnUpdatedUser() {
        UserResponseDto user = userService.createUser(new UserRequestDto("John Smith", "actor@example.com"));

        UserResponseDto response = userService.updateUser(
                new UserRequestDto("Elon Musk", "elon.musk@example.com"),
                user.getId());

        assertEquals(user.getId(), response.getId(), "Ids of same user should equals");
        assertNotEquals(user.getName(), response.getName(), "Name was updated and should not equals");
        assertNotEquals(user.getEmail(), response.getEmail(), "Email was updated and should not equals");
    }

    @Test
    void updateUser_whenUserDoesntExists_shouldThrowUserNotFoundException() {
        UserRequestDto userUpdateData = new UserRequestDto("New username", null);
        long userId = 10L;

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(
                        userUpdateData, userId));

        assertTrue(exception.getMessage().contains(String.valueOf(userId)),
                "Exception message should contain id of not found user");
    }
}
