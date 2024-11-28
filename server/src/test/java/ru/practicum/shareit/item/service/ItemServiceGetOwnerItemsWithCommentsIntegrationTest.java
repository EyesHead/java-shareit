package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentPostDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDetailedResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
class ItemServiceGetOwnerItemsWithCommentsIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentService commentService;

    private User owner;
    private User renter1;
    private User renter2;
    private Item item;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        // Создаем пользователей с уникальными email
        owner = userRepository.save(new User(null, "Test owner", "owner-" + System.nanoTime() + "@example.com"));
        renter1 = userRepository.save(new User(null, "Test renter 1", "renter1-" + System.nanoTime() + "@example.com"));
        renter2 = userRepository.save(new User(null, "Test renter 2", "renter2-" + System.nanoTime() + "@example.com"));

        // Создаем вещь
        item = itemRepository.save(
                new Item(
                        null,
                        "Item 1",
                        "Item 1 description",
                        true,
                        owner,
                        null,
                        null,
                        null
                )
        );

        // Создаем сразу подтвержденные бронирования
        booking1 = bookingRepository.save(new Booking(
                null,
                renter1,
                item,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED
        ));

        booking2 = bookingRepository.save(new Booking(
                null,
                renter2,
                item,
                LocalDateTime.now().minusHours(5),
                LocalDateTime.now().minusHours(12),
                BookingStatus.APPROVED
        ));

        // Создаем комментарии
        commentService.createComment(new CommentPostDto("Great item!"), renter1.getId(), item.getId());
        commentService.createComment(new CommentPostDto("Very useful!"), renter2.getId(), item.getId());
    }

    @Test
    void getOwnerItemsWithComments_whenSaveItemWithoutComments_shouldReturnItemWithoutComments() {
        // Создаем новую вещь без комментариев
        Item itemWithoutComments = itemRepository.save(
                new Item(
                        null,
                        "Item without comments",
                        "Description no comments",
                        true,
                        owner,
                        null,
                        null,
                        null
                )
        );

        var itemsWithoutComments = itemService.getOwnerItemsWithComments(owner.getId());

        assertThat(itemsWithoutComments).isNotNull();
        assertThat(itemsWithoutComments).hasSize(2);

        ItemDetailedResponseDto retrievedItemWithoutComments = itemsWithoutComments.stream()
                .filter(i -> i.getName().equals(itemWithoutComments.getName()))
                .findFirst()
                .orElse(null);

        assertThat(retrievedItemWithoutComments).isNotNull();
        assertThat(retrievedItemWithoutComments.getComments()).isEmpty();
    }

    @Test
    void getOwnerItemsWithComments_whenOwnerHasNoItems_shouldReturnEmptyList() {
        // Создаем нового пользователя без вещей
        User newOwner = userRepository.save(
                new User(
                        null,
                        "New Owner",
                        "newowner@example.com"
                )
        );

        var itemsWithComments = itemService.getOwnerItemsWithComments(newOwner.getId());

        assertThat(itemsWithComments).isNotNull();
        assertThat(itemsWithComments).isEmpty();
    }

    @Test
    void getOwnerItemsWithComments_whenItemUnavailable_shouldIncludeUnavailableItem() {
        // Создаем вещь с флагом доступности false
        Item unavailableItem = itemRepository.save(
                new Item(null,
                        "Item 3",
                        "Description 3",
                        false,
                        owner,
                        null,
                        null,
                        null
                )
        );

        var itemsWithComments = (List<ItemDetailedResponseDto>) itemService.getOwnerItemsWithComments(owner.getId());

        assertThat(itemsWithComments).isNotNull();
        assertThat(itemsWithComments).hasSize(2);

        ItemDetailedResponseDto retrievedUnavailableItem = itemsWithComments.stream()
                .filter(i -> i.getName().equals("Item 3"))
                .findFirst()
                .orElse(null);

        assertThat(retrievedUnavailableItem).isNotNull();
        assertThat(retrievedUnavailableItem.getAvailable()).isFalse();
    }
}