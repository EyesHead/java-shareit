package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker1;
    private User booker2;
    private Item item;
    private Booking pastBooking;
    private Booking currentBooking;

    @BeforeEach
    void setUp() {
        // Создаем пользователей
        owner = userRepository.save(new User(null, "Owner", "owner@example.com"));
        booker1 = userRepository.save(new User(null, "Booker1", "booker1@example.com"));
        booker2 = userRepository.save(new User(null, "Booker2", "booker2@example.com"));

        // Создаем вещь
        item = itemRepository.save(new Item(null, "Item", "Item Description", true, owner, null, null, null));

        // Создаем бронирования с разными пользователями
        pastBooking = bookingRepository.save(new Booking(
                null, booker1, item,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                BookingStatus.APPROVED
        ));

        currentBooking = bookingRepository.save(new Booking(
                null, booker2, item,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                BookingStatus.APPROVED
        ));
    }

    @Test
    void findByIdAndItemOwnerId_shouldReturnBooking_whenBookingExists() {
        Optional<Booking> result = bookingRepository.findByIdAndItemOwnerId(currentBooking.getId(), owner.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void findByBookerIdAndEndBefore_shouldReturnPastBooking() {
        List<Booking> result = bookingRepository.findByBookerIdAndEndBefore(
                booker1.getId(),
                LocalDateTime.now(),
                Sort.by(Sort.Direction.ASC, "end")
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void save_shouldThrowException_whenSameUserTriesToBookSameItem() {
        // Проверяем, что добавление бронирования для одного пользователя на ту же вещь вызывает исключение
        Booking duplicateBooking = new Booking(
                null, booker1, item,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(4),
                BookingStatus.APPROVED
        );

        assertThatThrownBy(() -> bookingRepository.saveAndFlush(duplicateBooking))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}