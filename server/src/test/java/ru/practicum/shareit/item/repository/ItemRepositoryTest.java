package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User otherUser;
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        otherUser = userRepository.save(new User(null, "OtherUser", "other@mail.com"));
    }

    @Test
    void findByIdAndOwnerId_whenItemExistsForOwner_shouldReturnItem() {
        // Создаем Item для владельца
        Item item = itemRepository.save(new Item(null, "Item 1", "Description 1", true, owner, null, null, null));

        // Проверяем, что метод находит Item по ID и владельцу
        Optional<Item> foundItem = itemRepository.findByIdAndOwnerId(item.getId(), owner.getId());
        assertTrue(foundItem.isPresent(), "Item should be found for the owner");
        assertEquals(item.getId(), foundItem.get().getId(), "Item ID should match");
    }

    @Test
    void findByIdAndOwnerId_whenItemNotOwnedByUser_shouldReturnEmpty() {
        // Создаем Item для другого владельца
        Item item = itemRepository.save(new Item(null, "Item 1", "Description 1", true, owner, null, null, null));

        // Проверяем, что метод не находит Item для другого владельца
        Optional<Item> foundItem = itemRepository.findByIdAndOwnerId(item.getId(), otherUser.getId());
        assertTrue(foundItem.isEmpty(), "Item should not be found for a different owner");
    }

    @Test
    void findBySearchText_whenNoMatches_shouldReturnEmptyCollection() {
        // Создаем Item для поиска
        itemRepository.save(new Item(null, "Item 1", "Description 1", true, owner, null, null, null));

        // Ищем по тексту, которого нет в названии или описании
        Collection<Item> items = itemRepository.findBySearchText("Non-existent text");
        assertTrue(items.isEmpty(), "Search should return an empty collection when no matches found");
    }

    @Test
    void findBySearchText_whenMatchesFound_shouldReturnMatchingItems() {
        // Создаем несколько Item
        Item item1 = itemRepository.save(new Item(null, "Item 1", "Description 1", true, owner, null, null, null));
        Item item2 = itemRepository.save(new Item(null, "Item 2", "Description 2", true, owner, null, null, null));
        itemRepository.save(new Item(null, "Item 3", "Special item", true, owner, null, null, null));

        // Ищем по тексту, который есть в названии
        Collection<Item> items = itemRepository.findBySearchText("Item 1");
        assertEquals(1, items.size(), "Search should return the correct item based on the name");

        // Ищем по тексту, который есть в описании
        items = itemRepository.findBySearchText("Description 2");
        assertEquals(1, items.size(), "Search should return the correct item based on the description");
    }

    @Test
    void findAllByOwnerIdWithComments_whenNoItems_shouldReturnEmptyCollection() {
        // Проверяем, что если у владельца нет предметов, то вернется пустой список
        Collection<Item> items = itemRepository.findAllByOwnerIdWithComments(owner.getId());
        assertTrue(items.isEmpty(), "Should return empty collection when the owner has no items");
    }

    @Test
    void findByIdWithComments_whenItemHasNoComments_shouldReturnItemWithCommentsEqualsNull() {
        // Создаем Item без комментариев
        Item item = itemRepository.save(new Item(null, "Item 1", "Description 1", true, owner, null, null, null));

        // Проверяем, что метод возвращает Item без комментариев
        Optional<Item> foundItem = itemRepository.findByIdWithComments(item.getId());
        assertTrue(foundItem.isPresent(), "Item should be found by ID");
        assertNull(foundItem.get().getComments(), "Item should have no comments");
    }
}
