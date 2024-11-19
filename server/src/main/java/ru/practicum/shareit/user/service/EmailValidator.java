package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EmailValidator {
    UserRepository userRepository;

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }
}
