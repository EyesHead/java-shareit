package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User responder;

    @BeforeEach
    void setUp() {
        requester = userRepository.save(new User(null, "Requester", "requester@mail.com"));
        responder = userRepository.save(new User(null, "OtherUser", "responder@mail.com"));
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc_whenNoRequests_shouldReturnEmptyList() {
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(requester.getId());

        assertTrue(requests.isEmpty(), "Should return empty list when there are no requests");
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc_whenRequestsExist_shouldReturnOrderedRequests() {
        ItemRequest request1 = requestRepository.save(new ItemRequest(null, "Request 1", LocalDateTime.now().minusDays(1), requester, null));
        ItemRequest request2 = requestRepository.save(new ItemRequest(null, "Request 2", LocalDateTime.now(), requester, null));

        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(requester.getId());

        assertEquals(2, requests.size(), "Should return all requests");
        assertEquals(request2.getId(), requests.get(0).getId(), "Requests should be ordered by creation date DESC");
        assertEquals(request1.getId(), requests.get(1).getId(), "Requests should be ordered by creation date DESC");
    }

    @Test
    void findByIdWithItems_whenRequestDoesNotExist_shouldReturnEmptyOptional() {
        Optional<ItemRequest> request = requestRepository.findByIdWithItems(1L);

        assertTrue(request.isEmpty(), "Should return empty Optional when request does not exist");
    }

    @Transactional
    @Test
    void findByIdWithItems_whenRequestExists_shouldReturnRequestWithItems() {
        // Создаём данные
        ItemRequest request = new ItemRequest(null, "Request description", LocalDateTime.now(), requester, null);
        request = requestRepository.save(request);

        Item item = new Item(null, "Item 1", "Description 1", true, responder, null, null, null);
        request.addItem(item); // Устанавливаем связь
        item = itemRepository.save(item);

        Long requestId = request.getId();

        // Выполняем запрос
        ItemRequest requestFound = requestRepository.findById(requestId).orElseThrow();

        // Проверяем данные
        assertNotNull(requestFound.getItems(), "Items не должны быть null");
        assertNotNull(requestFound.getItems(), "Items не должны быть null");
        assertEquals(1, requestFound.getItems().size(), "Должен быть ровно 1 item");
        assertEquals(item.getName(), requestFound.getItems().getFirst().getName(), "Поле name одного и того же предмета не должно отличаться");
    }
}