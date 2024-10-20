package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataIntegrityException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Optional;

@Repository("inMemory")
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public boolean isEmailAlreadyUsed(String email) {
        boolean exists = users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
        log.debug("Checking if email '{}' is already used: {}", email, exists);
        return exists;
    }

    @Override
    public boolean isUserExist(long id) {
        boolean exists = users.containsKey(id);
        log.debug("Checking if user with id {} exists: {}", id, exists);
        return exists;
    }

    @Override
    public User createUser(User user) {
        long newId = generatePrimaryKey();
        User createdUser = user.toBuilder()
                .id(newId)
                .build();
        users.put(newId, createdUser);
        log.info("User created with id {}: {}", newId, createdUser);
        return createdUser;
    }

    @Override
    public User updateUser(User user) {
        long userId = user.getId();
        User userForUpdate = users.get(userId);

        if (userForUpdate == null) {
            log.warn("Update error: User with id {} not found", userId);
            throw new DataIntegrityException("User with id = '" + userId + "' should exist");
        }

        User updatedUser = userForUpdate.toBuilder()
                .name(user.getName() != null ? user.getName() : userForUpdate.getName())
                .email(user.getEmail() != null ? user.getEmail() : userForUpdate.getEmail())
                .build();
        users.put(userId, updatedUser);
        log.info("User updated with id {}: {}", userId, updatedUser);
        return updatedUser;
    }

    @Override
    public Optional<User> findUser(long id) {
        User foundUser = users.get(id);
        log.debug("Finding user with id {}: {}", id, foundUser);
        return Optional.ofNullable(foundUser);
    }

    @Override
    public boolean deleteUser(long id) {
        boolean deleted = users.remove(id) != null;
        if (deleted) {
            log.info("User with id {} deleted successfully", id);
        } else {
            log.warn("Delete failed: User with id {} not found", id);
        }
        return deleted;
    }

    private long generatePrimaryKey() {
        return ++id;
    }
}