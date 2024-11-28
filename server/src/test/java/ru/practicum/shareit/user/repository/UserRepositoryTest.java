package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    void existsByEmailIgnoreCase_whenDataBaseIsEmpty_shouldReturnFalse() {
        boolean isExist = repository.existsByEmailIgnoreCase("kdv1@mail.ru");

        assertFalse(isExist, "Should return false with empty data base");
    }

    @Test
    void existsByEmailIgnoreCase_whenEmailExistsIgnoringCase_shouldReturnTrue() {
        User user = new User(null, "Daniil", "d.k@ya.ru");
        repository.save(user);

        boolean isExist = repository.existsByEmailIgnoreCase("D.k@Ya.RU");

        assertTrue(isExist, "Should return true");
    }

    @Test
    void existsByEmailIgnoreCase_whenEmailDoesNotExist_shouldReturnFalse() {
        User user = new User(null, "Daniil", "d.k@ya.ru");
        repository.save(user);

        String nonExistingEmail = user.getEmail() + "dsqwrf";
        boolean isExist = repository.existsByEmailIgnoreCase(nonExistingEmail);

        assertFalse(isExist, "Should return false with non existing email = " + nonExistingEmail);
    }

    @Test
    void existsByEmailIgnoreCase_whenEmailExistsWithMultipleUsers_shouldReturnTrue() {
        User user1 = new User(null, "Daniil", "d.k@ya.ru");
        User user2 = new User(null, "Sasha", "s.hs@ya.ru");
        User user3 = new User(null, "Egor", "e.edv@ya.ru");

        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        String email = user3.getEmail();
        boolean isExist = repository.existsByEmailIgnoreCase(email);

        assertTrue(isExist, "Should return true with existing email = " + email);
    }
}