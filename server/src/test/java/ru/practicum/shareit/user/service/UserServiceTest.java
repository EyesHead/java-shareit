package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.EmailAlreadyExistsException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EmailValidator emailValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john.doe@example.com");
        userRequestDto = new UserRequestDto("John Doe", "john.doe@example.com");
        userResponseDto = new UserResponseDto(1L, "John Doe", "john.doe@example.com");
    }

    @Test
    void createUser_shouldReturnUserResponseDto_whenUserCreatedSuccessfully() {
        // Arrange
        when(userRepository.existsByEmailIgnoreCase(userRequestDto.getEmail())).thenReturn(false);
        when(userMapper.toUser(userRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.createUser(userRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDto, result);
        verify(userRepository).existsByEmailIgnoreCase(userRequestDto.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldThrowEmailAlreadyExistsException_whenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmailIgnoreCase(userRequestDto.getEmail())).thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(userRequestDto));

        // Проверка, что сообщение об ошибке содержит правильный email
        assertTrue(exception.getMessage().contains(userRequestDto.getEmail()));
    }

    @Test
    void updateUser_whenUpdateUserEmailWithExistingEmail_shouldThrowEmailAlreadyExistsException() {
        long userId = user.getId();
        String newEmail = "SomeMail@example.com";
        UserRequestDto updateRequestDto = new UserRequestDto(null, newEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(emailValidator.isEmailTaken(newEmail)).thenReturn(true);

        EmailAlreadyExistsException thrown = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUser(updateRequestDto, userId);
        });
        assertTrue(thrown.getMessage().contains(newEmail), "Error message should contain not valid email");
        verify(emailValidator, times(1)).isEmailTaken(newEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_whenUserNotFound_shouldThrowUserNotFoundException() {
        // Arrange
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(userId));

        // Проверка, что сообщение об ошибке содержит правильный userId
        assertTrue(exception.getMessage().contains(String.valueOf(userId)));
        verify(userRepository, Mockito.times(1)).findById(userId);
    }

    @Test
    void getUserById_shouldReturnUserResponseDto_whenUserExists() {
        // Arrange
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDto, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // Arrange
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(userId));

        // Проверка, что сообщение исключения содержит правильный userId
        assertTrue(exception.getMessage().contains(String.valueOf(userId)));
    }

    @Test
    void removeUserById_shouldDeleteUser_whenUserExists() {
        // Arrange
        long userId = 1L;

        // Act
        userService.removeUserById(userId);

        // Assert
        verify(userRepository).deleteById(userId);
    }
}
