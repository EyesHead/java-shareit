package ru.practicum.shareit.user.service;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserPostDto;
import ru.practicum.shareit.user.repository.UserRepository;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserDtoMapper userDtoMapper;

    @Autowired
    public UserServiceImpl(@Qualifier("inMemory") UserRepository userRepository, UserDtoMapper userDtoMapper) {
        this.userRepository = userRepository;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public UserResponseDto createUser(UserPostDto userPostDto) throws EmailAlreadyExistsException {
        String email = userPostDto.getEmail();
        if (userRepository.isEmailAlreadyUsed(email)) {
            log.warn("Attempt to create user failed: Email {} already exists", email);
            throw new EmailAlreadyExistsException("Create error. Email " + email + " already exists");
        }

        User user = userDtoMapper.toUser(userPostDto);
        User userCreated = userRepository.createUser(user);
        log.info("User created with id {}: {}", userCreated.getId(), userCreated);
        return userDtoMapper.toDto(userCreated);
    }

    @Override
    public UserResponseDto updateUser(UserPatchDto userPatchDto, long id) {
        if (!userRepository.isUserExist(id)) {
            log.warn("Update error: User with id = '{}' doesn't exist", id);
            throw new UserNotFoundException(id);
        }

        String email = userPatchDto.getEmail();
        if (email != null && userRepository.isEmailAlreadyUsed(email)) {
            log.warn("Attempt to update user failed: Email {} already exists", email);
            throw new EmailAlreadyExistsException("Update error. Email " + email + " already exists");
        }

        User user = userDtoMapper.toUser(userPatchDto);
        User userForUpdate = user.toBuilder().id(id).build();
        User updatedUser = userRepository.updateUser(userForUpdate);
        log.info("User updated with id {}: {}", updatedUser.getId(), updatedUser);
        return userDtoMapper.toDto(updatedUser);
    }

    @Override
    public UserResponseDto getUserById(long id) {
        User userFound = userRepository.findUser(id)
                .orElseThrow(() -> {
                    log.warn("User not found. Id = {}", id);
                    return new UserNotFoundException(id);
                });
        log.info("User found with id {}: {}", id, userFound);
        return userDtoMapper.toDto(userFound);
    }

    @Override
    public void removeUserById(long id) {
        if (userRepository.deleteUser(id)) {
            log.info("User deleted with id {}", id);
        } else {
            log.warn("Attempt to delete user failed: User not found. Id = {}", id);
            throw new UserNotFoundException(id);
        }
    }
}