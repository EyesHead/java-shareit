package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.comment.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.ResponseOnItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void toSimpleResponseList_shouldReturnEmptyCollection_whenNull() {
        // Arrange
        Collection<ItemRequest> itemRequests = null;

        // Act
        Collection<ItemRequestSimpleDto> result = itemRequestMapper.toSimpleResponseList(itemRequests);

        // Assert
        assertNull(result, "Result should be null when input collection is null");
    }

    @Test
    void toSimpleResponseList_shouldReturnEmptyCollection_whenEmpty() {
        // Arrange
        Collection<ItemRequest> itemRequests = Collections.emptyList();

        // Act
        Collection<ItemRequestSimpleDto> result = itemRequestMapper.toSimpleResponseList(itemRequests);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be an empty collection");
    }

    @Test
    void toSimpleResponseList_shouldMapCorrectly_whenNonEmpty() {
        // Arrange
        ItemRequest itemRequest1 = new ItemRequest(1L, "Description 1", LocalDateTime.now(), new User(), new ArrayList<>());
        ItemRequest itemRequest2 = new ItemRequest(2L, "Description 2", LocalDateTime.now(), new User(), new ArrayList<>());
        Collection<ItemRequest> itemRequests = Arrays.asList(itemRequest1, itemRequest2);

        // Act
        Collection<ItemRequestSimpleDto> result = itemRequestMapper.toSimpleResponseList(itemRequests);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "The collection size should match the input size");
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(1L)), "First item request should be mapped correctly");
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(2L)), "Second item request should be mapped correctly");
    }

    @Test
    void toSimpleResponse_shouldHandleNull() {
        // Act
        ItemRequestSimpleDto result = itemRequestMapper.toSimpleResponse(null);

        // Assert
        assertNull(result, "Result should be null when input is null");
    }

    @Test
    void itemToItemResponseShortDto_shouldMapCorrectly() {
        // Arrange
        Item item = new Item(1L,
                "Item Name",
                "Description",
                true,
                new User(1L, "username", "username@email.com"),
                new ItemRequest(1L, "Item Request Description", LocalDateTime.now(), new User(), new ArrayList<>()),
                List.of(new Booking()),
                List.of(new Comment())
                );

        // Act
        ResponseOnItemRequestDto result = itemRequestMapper.itemToItemResponseShortDto(item);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getId(), "The item ID should be mapped correctly");
        assertEquals("Item Name", result.getName(), "The item name should be mapped correctly");
        assertEquals(1L, result.getOwnerId(), "The owner ID should be mapped correctly");
    }

    @Test
    void toDetailedResponseList_shouldHandleNull() {
        // Arrange
        Collection<ItemRequest> itemRequests = null;

        // Act
        Collection<ItemRequestWithResponsesDto> result = itemRequestMapper.toDetailedResponseList(itemRequests);

        // Assert
        assertNull(result, "Result should be null when input collection is null");
    }

    @Test
    void toDetailedResponse_shouldMapCorrectly() {
        // Arrange
        ItemRequest itemRequest = new ItemRequest(
                1L,
                "Request Description",
                LocalDateTime.now(),
                new User(),
                List.of(Item.builder().id(1L).build()));
        ResponseOnItemRequestDto itemDto = new ResponseOnItemRequestDto(1L, "Item Name", 1L);
        ItemRequestWithResponsesDto expectedDto = new ItemRequestWithResponsesDto(1L, "Request Description", LocalDateTime.now(), List.of(itemDto));

        // Act
        ItemRequestWithResponsesDto result = itemRequestMapper.toDetailedResponse(itemRequest);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getId(), "The ID should be mapped correctly");
        assertEquals("Request Description", result.getDescription(), "The description should be mapped correctly");
        assertEquals(expectedDto.getItems().size(), result.getItems().size(), "The items should be correctly mapped");
    }
}