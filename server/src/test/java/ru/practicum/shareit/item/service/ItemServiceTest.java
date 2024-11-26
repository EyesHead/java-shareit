package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.entity.Comment;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDetailedResponseDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@example.com");
        item = new Item(1L, "Item", "item description", true, owner, null, null, null);
    }

    @Test
    void getItemWithCommentsById_whenUserNotFound_shouldThrowUserNotFoundException() {
        long itemId = item.getId();
        long userId = owner.getId();

        // Подготовка данных
        when(userRepository.existsById(userId)).thenReturn(false);

        // Проверка на исключение
        assertThrows(UserNotFoundException.class, () -> itemService.getItemWithCommentsById(itemId, userId));

        verify(itemRepository, never()).findByIdWithComments(itemId);
    }

    @Test
    void getItemWithCommentsById_whenItemHasComments_shouldReturnItemWithComments() {
        long itemId = item.getId();
        long userId = owner.getId();

        // Подготовка данных
        Comment comment1 = new Comment(1L, item, owner, "Comment 1", LocalDateTime.now());
        Comment comment2 = new Comment(2L, item, owner, "Comment 2", LocalDateTime.now());
        item.setComments(List.of(comment1, comment2));

        ItemDetailedResponseDto expectedDto = new ItemDetailedResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                List.of(
                        new CommentResponseDto(comment1.getId(), comment1.getText(), comment1.getAuthor().getName(), comment1.getCreated()),
                        new CommentResponseDto(comment2.getId(), comment2.getText(), comment2.getAuthor().getName(), comment2.getCreated())
                ),
                null,
                null
        );

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findByIdWithComments(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toDetailedResponse(item)).thenReturn(expectedDto);

        // Выполнение метода
        ItemDetailedResponseDto result = itemService.getItemWithCommentsById(itemId, userId);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(userRepository).existsById(userId);
        verify(itemRepository).findByIdWithComments(itemId);
        verify(itemMapper).toDetailedResponse(item);
    }

    @Test
    void getItemWithCommentsById_whenItemNotFound_shouldThrowItemNotFoundException() {
        long itemId = item.getId();
        long userId = owner.getId();

        // Подготовка данных
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findByIdWithComments(itemId)).thenReturn(Optional.empty());

        // Проверка на исключение
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemWithCommentsById(itemId, userId));

        verify(itemRepository).findByIdWithComments(itemId);
    }

    @Test
    void searchUserItemsBySearchText_whenUserExistsAndItemsFound_shouldReturnItems() {
        long userId = owner.getId();
        String searchText = "item";

        // Подготовка данных
        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findBySearchText(searchText)).thenReturn(List.of(item));
        when(itemMapper.toResponse(item)).thenReturn(new ItemResponseDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable()));

        Collection<ItemResponseDto> response = itemService.searchUserItemsBySearchText(userId, searchText);

        // Проверка
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(item.getId(), response.iterator().next().getId());

        verify(itemRepository).findBySearchText(searchText);
        verify(userRepository).existsById(userId);
    }

    @Test
    void searchUserItemsBySearchText_whenUserNotFound_shouldThrowUserNotFoundException() {
        long userId = owner.getId();
        String searchText = "item";

        // Подготовка данных
        when(userRepository.existsById(userId)).thenReturn(false);

        // Проверка на исключение
        assertThrows(UserNotFoundException.class, () -> itemService.searchUserItemsBySearchText(userId, searchText));

        verify(itemRepository, never()).findBySearchText(searchText);
    }

    @Test
    void getOwnerItemsWithComments_whenUserExists_shouldReturnItemsWithComments() {
        long userId = owner.getId();

        // Подготовка данных
        Item item1 = new Item(1L, "Item1", "Description1", true, owner, null, null, new ArrayList<>());
        Item item2 = new Item(2L, "Item2", "Description2", true, owner, null, null, new ArrayList<>());
        List<Item> items = List.of(item1, item2);

        ItemDetailedResponseDto itemDto1 = new ItemDetailedResponseDto(1L, "Item1", "Description1", true, List.of(), null, null);
        ItemDetailedResponseDto itemDto2 = new ItemDetailedResponseDto(2L, "Item2", "Description2", true, List.of(), null, null);
        List<ItemDetailedResponseDto> itemDtos = List.of(itemDto1, itemDto2);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllByOwnerIdWithComments(userId)).thenReturn(items);
        when(itemMapper.toDetailedResponseList(items)).thenReturn(itemDtos);

        // Выполнение метода
        Collection<ItemDetailedResponseDto> result = itemService.getOwnerItemsWithComments(userId);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(itemDto1));
        assertTrue(result.contains(itemDto2));

        verify(userRepository).existsById(userId);
        verify(itemRepository).findAllByOwnerIdWithComments(userId);
        verify(itemMapper).toDetailedResponseList(items);
    }

    @Test
    void getOwnerItemsWithComments_whenUserNotFound_shouldThrowUserNotFoundException() {
        long userId = owner.getId();

        // Подготовка данных
        when(userRepository.existsById(userId)).thenReturn(false);

        // Проверка на исключение
        assertThrows(UserNotFoundException.class, () -> itemService.getOwnerItemsWithComments(userId));

        verify(itemRepository, never()).findAllByOwnerIdWithComments(userId);
    }

    @Test
    void updateItemByIdAndOwnerId_whenItemExistsAndUserIsOwner_shouldUpdateItem() {
        long itemId = item.getId();
        long ownerId = owner.getId();
        ItemPatchDto itemPatchDto = new ItemPatchDto("Updated name", "Updated description", true);
        Item itemUpdated = new Item(
                itemId,
                itemPatchDto.getName(),
                itemPatchDto.getDescription(),
                true,
                owner,
                null,
                null,
                new ArrayList<>());

        // Подготовка данных
        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(itemRepository.findByIdAndOwnerId(itemId, ownerId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(itemUpdated);
        when(itemMapper.toResponse(any(Item.class))).thenReturn(new ItemResponseDto(
                itemUpdated.getId(),
                itemUpdated.getName(),
                itemUpdated.getDescription(),
                itemUpdated.getAvailable()
        ));

        ItemResponseDto response = itemService.updateItemByIdAndOwnerId(ownerId, itemId, itemPatchDto);

        // Проверка
        assertNotNull(response);
        assertEquals(item.getId(), response.getId());
        assertEquals("Updated name", response.getName());
        assertEquals("Updated description", response.getDescription());
        assertTrue(response.getAvailable());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItemByIdAndOwnerId_whenUserNotFound_shouldThrowUserNotFoundException() {
        long itemId = item.getId();
        long ownerId = owner.getId();
        ItemPatchDto itemPatchDto = new ItemPatchDto("Updated name", "Updated description", true);

        // Подготовка данных
        when(userRepository.existsById(ownerId)).thenReturn(false);

        // Проверка на исключение
        assertThrows(UserNotFoundException.class, () -> itemService.updateItemByIdAndOwnerId(ownerId, itemId, itemPatchDto));

        verify(itemRepository, never()).findByIdAndOwnerId(itemId, ownerId);
    }

    @Test
    void createItemByUser_whenUserExists_shouldCreateItem() {
        long userId = owner.getId();
        ItemPostDto itemPostDto = new ItemPostDto("New Item", "Description of new item", true, null);

        // Подготовка данных
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemMapper.toEntity(itemPostDto)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toResponse(item)).thenReturn(new ItemResponseDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable()));

        ItemResponseDto response = itemService.createItemByUser(userId, itemPostDto);

        // Проверка
        assertNotNull(response);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertTrue(response.getAvailable());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItemByUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        long userId = owner.getId();
        ItemPostDto itemPostDto = new ItemPostDto("New Item", "Description of new item", true, null);

        // Подготовка данных
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Проверка на исключение
        assertThrows(UserNotFoundException.class, () -> itemService.createItemByUser(userId, itemPostDto));

        verify(itemRepository, never()).save(any(Item.class));
    }
}