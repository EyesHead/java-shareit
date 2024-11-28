package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
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
    ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemResponseDto createItemByUser(long userId, ItemPostDto itemPost) {
        log.info("[SERVER | SERVICE] Invoking createItemByUser method.");
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        log.trace("[SERVER | SERVICE] Owner found: {}", owner);

        Item item = itemMapper.toEntity(itemPost);
        item.setOwner(owner);
        log.debug("[SERVER | SERVICE] Item prepared for saving: {}", item);

        Item savedItem = itemRepository.save(item);
        log.trace("[SERVER | SERVICE] Item saved: {}", savedItem);

        log.info("[SERVER | SERVICE] Item successfully created for user. ItemId = {}, userId = {}", savedItem.getId(), userId);
        return itemMapper.toResponse(savedItem);
    }

    @Override
    public ItemResponseDto createItemOnRequest(long userId, long requestId, ItemPostDto itemPost) {
        log.info("[SERVER | SERVICE] Invoking createItemByRequest method of ItemService");
        Item item = itemMapper.toEntity(itemPost);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        item.setOwner(owner);
        log.trace("[SERVER | SERVICE] Owner was found and saved to item as field: {}", item);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(requestId));

        item.setRequest(itemRequest);
        itemRequest.addItem(item);

        log.trace("[SERVER | SERVICE] Item request was found and saved to item as field: {}", item);

        Item itemSaved = itemRepository.save(item);
        Hibernate.initialize(itemSaved.getRequest());
        log.trace("[SERVER | SERVICE] Item was successfully saved into db: {}", itemSaved);

        log.info("[SERVER | SERVICE] createItemByRequest was successfully invoked");
        return itemMapper.toResponse(itemSaved);
    }

    @Transactional
    @Override
    public ItemResponseDto updateItemByIdAndOwnerId(long ownerId, long itemId, ItemPatchDto updateData) {
        log.info("[SERVER | SERVICE] Invoking updateUserItem method.");
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }

        Item itemFound = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        Item itemToUpdate = itemFound.toBuilder()
                .name(updateData.getName() == null ? itemFound.getName() : updateData.getName())
                .description(updateData.getDescription() == null ? itemFound.getDescription() : updateData.getDescription())
                .available(updateData.getAvailable() == null ? itemFound.getAvailable() : updateData.getAvailable())
                .build();

        log.debug("[SERVER | SERVICE] Item data for update: {}", itemToUpdate);

        Item updatedItem = itemRepository.save(itemToUpdate);
        log.trace("[SERVER | SERVICE] Item updated in DB: {}", updatedItem);

        log.info("[SERVER | SERVICE] Item with ID = {} successfully updated for user with ID = {}", itemId, ownerId);
        return itemMapper.toResponse(updatedItem);
    }

    @Transactional
    @Override
    public Collection<ItemDetailedResponseDto> getOwnerItemsWithComments(long userId) {
        log.info("[SERVER | SERVICE] Invoking getOwnerItemsWithComments method.");
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<Item> items = (List<Item>) itemRepository.findAllByOwnerIdWithComments(userId);
        log.debug("[SERVER | SERVICE] Items retrieved for owner with ID = {}: {}", userId, items);

        List<ItemDetailedResponseDto> detailedItems = itemMapper.toDetailedResponseList(items);

        log.info("[SERVER | SERVICE] Successfully retrieved {} items with comments for user with ID = {}", items.size(), userId);
        return detailedItems;
    }

    @Transactional
    @Override
    public ItemDetailedResponseDto getItemWithCommentsById(long itemId, long userId) {
        log.info("[SERVER | SERVICE] Invoking getItemWithCommentsById method.");
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Item foundItem = itemRepository.findByIdWithComments(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        log.debug("[SERVER | SERVICE] Item retrieved: {}", foundItem);

        log.info("[SERVER | SERVICE] Successfully retrieved item with ID = {}", itemId);
        return itemMapper.toDetailedResponse(foundItem);
    }

    @Transactional
    @Override
    public Collection<ItemResponseDto> searchUserItemsBySearchText(long userId, String searchText) {
        log.info("[SERVER | SERVICE] Invoking searchUserItemsBySearchText method.");
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Collection<Item> foundItems = itemRepository.findBySearchText(searchText);
        log.debug("[SERVER | SERVICE] Items found by search text '{}': {}", searchText, foundItems);

        Collection<ItemResponseDto> response = foundItems.stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toResponse)
                .toList();

        log.info("[SERVER | SERVICE] Successfully retrieved search results for user with ID = {}", userId);
        return response;
    }
}