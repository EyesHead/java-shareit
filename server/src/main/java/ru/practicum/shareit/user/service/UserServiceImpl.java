package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EmailAlreadyExistsException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    EmailValidator emailValidator;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) throws EmailAlreadyExistsException {
        String email = userRequestDto.getEmail();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.warn("Attempt to create user failed: Email {} already exists", email);
            throw new EmailAlreadyExistsException(email);
        }

        User user = userMapper.toUser(userRequestDto);
        User savedUser = userRepository.save(user);
        log.info("User created with id {}: {}", savedUser.getId(), savedUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto updateUser(UserRequestDto userRequestDto, long userId) {
        // Поиск в БД обновляемого пользователя
        User userForUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Проверка на занятость email
        String requestEmail = userRequestDto.getEmail();
        if (requestEmail != null) {
            if (emailValidator.isEmailTaken(requestEmail)) {
                throw new EmailAlreadyExistsException(requestEmail);
            }
            userForUpdate.setEmail(requestEmail);
        }

        // Обновление имени, если оно не null
        if (userRequestDto.getName() != null) {
            userForUpdate.setName(userRequestDto.getName());
        }

        // Сохранение обновленного пользователя в БД
        User updatedUser = userRepository.save(userForUpdate);

        // Логирование успешного обновления
        log.info("User with id = {} was successfully updated", userId);

        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserResponseDto getUserById(long userId) {
        User userFound = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        log.info("User was found with userId = {}", userId);
        return userMapper.toDto(userFound);
    }

    @Override
    public void removeUserById(long userId) {
        userRepository.deleteById(userId);
    }
}