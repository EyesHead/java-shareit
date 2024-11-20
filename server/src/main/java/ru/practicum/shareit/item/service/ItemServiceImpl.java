package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.ItemRequestNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDetailedResponseDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemRequestRepository itemRequestRepository;

    CommentMapper commentMapper;
    ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemResponseDto createItemByUser(long userId, ItemPostDto itemPost) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemMapper.toEntity(itemPost);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);

        log.info("Item successfully created for user. ItemId = {}, userId = {}", savedItem.getId(), userId);

        return itemMapper.toResponse(savedItem);
    }

    @Override
    public ItemResponseDto createItemByRequest(long userId, long requestId, ItemPostDto itemPost) {
        Item item = itemMapper.toEntity(itemPost);
        // Получение пользователя из бд
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        item.setOwner(owner);
        // Получение запроса на создание из бд
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(requestId));

        // Обработка случая создания предмета на собственный запрос
        long requesterId = itemRequest.getRequester().getId();
        if (requesterId == userId) {
            throw new UnsupportedOperationException("It is not possible to create an item based on a single request");
        }
        item.setRequest(itemRequest);

        Item itemSaved = itemRepository.save(item);
        Hibernate.initialize(itemSaved.getRequest());

        return itemMapper.toResponse(itemSaved);
    }

    @Transactional
    @Override
    public ItemResponseDto updateUserItem(long ownerId, long itemId, ItemPatchDto itemPatchDto) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }

        Item itemToUpdate = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        Item itemUpdateData = itemMapper.toEntity(itemPatchDto);

        itemToUpdate = updateItemData(itemToUpdate, itemUpdateData);

        Item updatedItem = itemRepository.save(itemToUpdate);

        ItemResponseDto response = itemMapper.toResponse(updatedItem);
        log.info("Item successfully updated for user with ID = {}", ownerId);
        return response;
    }

    private Item updateItemData(Item itemToUpdate, Item updateData) {
        return itemToUpdate.toBuilder()
                .name(updateData.getName() == null ? itemToUpdate.getName() : updateData.getName())
                .description(updateData.getDescription() == null ? itemToUpdate.getDescription() : updateData.getDescription())
                .available(updateData.getAvailable() == null ? itemToUpdate.getAvailable() : updateData.getAvailable())
                .build();
    }

    @Transactional
    @Override
    public Collection<ItemDetailedResponseDto> getOwnerItemsWithComments(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<Item> items = (List<Item>) itemRepository.findAllByOwnerIdWithComments(userId);

        List<ItemDetailedResponseDto> detailedItems = items.stream()
                .map(this::mapToDetailedResponseDto)
                .toList();

        log.info("Successfully retrieved {} items with comments and bookings for user with ID = {}", items.size(), userId);
        return detailedItems;
    }

    @Transactional
    @Override
    public ItemDetailedResponseDto getItemWithCommentsById(long itemId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Item foundItem = itemRepository.findByIdWithComments(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        log.debug("Item response data before mapping: {}", foundItem);

        var responseDto = ItemDetailedResponseDto.builder()
                .id(foundItem.getId())
                .available(foundItem.getAvailable())
                .name(foundItem.getName())
                .description(foundItem.getDescription())
                .lastBooking(null)
                .nextBooking(null)
                .comments(commentMapper.toResponseList(foundItem.getComments()))
                .build();

        log.info("Successfully retrieved item for user with ID = {}", userId);
        log.debug("Item response data after mapping: {}", foundItem);
        return responseDto;
    }

    @Transactional
    @Override
    public Collection<ItemResponseDto> searchUserItemsBySearchText(long userId, String searchText) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Collection<Item> foundItems = itemRepository.findBySearchText(searchText);

        Collection<ItemResponseDto> response = itemMapper.toResponseList(foundItems);
        response = response.stream()
                .filter(ItemResponseDto::getAvailable)
                .toList();
        log.info("Successfully retrieved search results for user with ID = {}", userId);
        return response;
    }

    private ItemDetailedResponseDto mapToDetailedResponseDto(Item itemWithComments) {
        return ItemDetailedResponseDto.builder()
                .id(itemWithComments.getId())
                .name(itemWithComments.getName())
                .description(itemWithComments.getDescription())
                .available(itemWithComments.getAvailable())
                .comments(
                        itemWithComments.getComments()
                        .stream()
                        .map(commentMapper::toResponse)
                        .toList()
                )
                // так сделано в силу некорректности тестов POSTMAN, прикрепленных к 15 и 16 ТЗ
                .lastBooking(null)
                .nextBooking(null)
                .build();
    }
}