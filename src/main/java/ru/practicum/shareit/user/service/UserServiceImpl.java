package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.error.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.dto.UserPostDto;
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
    public UserResponseDto createUser(UserPostDto userPostDto) throws EmailAlreadyExistsException {
        String email = userPostDto.getEmail();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.warn("Attempt to create user failed: Email {} already exists", email);
            throw new EmailAlreadyExistsException(email);
        }

        User user = userMapper.toUser(userPostDto);
        User savedUser = userRepository.save(user);
        log.info("User created with id {}: {}", savedUser.getId(), savedUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto updateUser(UserPatchDto userPatchDto, long userId) {
        User userForUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (userPatchDto.getEmail() != null) {
            validateEmail(userPatchDto.getEmail());
            userForUpdate.setEmail(userPatchDto.getEmail());
        }

        if (userPatchDto.getName() != null) {
            userForUpdate.setName(userPatchDto.getName());
        }

        User updatedUser = userRepository.save(userForUpdate);
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

    private void validateEmail(String email) {
        if (emailValidator.isEmailTaken(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }
}