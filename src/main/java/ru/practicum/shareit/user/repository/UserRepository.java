package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    boolean isEmailAlreadyUsed(String email);

    boolean isUserExist(long id);

    User createUser(User user);

    User updateUser(User user);

    Optional<User> findUser(long id);

    boolean deleteUser(long id);
}
