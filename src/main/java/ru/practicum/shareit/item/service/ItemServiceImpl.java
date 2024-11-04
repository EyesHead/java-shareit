package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ItemOfUserNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemDtoMapper itemDtoMapper;

    @Override
    public ItemResponseDto createItemForUser(long userId, ItemPostDto itemRequest) {
        if (!userRepository.isUserExist(userId)) {
            throw new UserNotFoundException(userId);
        }

        Item createdItem = itemRepository.addItemToUser(userId, itemDtoMapper.toItem(itemRequest));
        log.debug("Item created: {}", createdItem);
        ItemResponseDto response = itemDtoMapper.toDto(createdItem);
        log.info("Item successfully created for user with ID {}: {}", userId, response);
        return response;
    }

    @Override
    public ItemResponseDto updateUserItem(long userId, long itemId, ItemPatchDto itemUpdate) {
        if (!userRepository.isUserExist(userId)) {
            throw new UserNotFoundException(userId);
        }

        if (!itemRepository.isItemOfUserExist(itemId, userId)) {
            throw new ItemOfUserNotFoundException(itemId, userId);
        }

        Item itemForUpdate = itemDtoMapper.toItem(itemUpdate);
        itemForUpdate.setId(itemId);

        Item updatedItem = itemRepository.updateUserItem(userId, itemId, itemForUpdate);
        log.debug("Item updated: {}", updatedItem);

        ItemResponseDto response = itemDtoMapper.toDto(updatedItem);
        log.info("Item successfully updated for user with ID {}: {}", userId, response);
        return response;
    }

    @Override
    public Collection<ItemResponseDto> getUserItems(long userId) {
        if (!userRepository.isUserExist(userId)) {
            throw new UserNotFoundException(userId);
        }

        Collection<Item> userItems = itemRepository.findUserItems(userId);

        Collection<ItemResponseDto> response = itemDtoMapper.toDto(userItems);
        log.info("Successfully retrieved items for user with ID {}: {}", userId, response);
        return response;
    }

    @Override
    public ItemResponseDto getUserItemByItemId(long userId, long itemId) {
        Item foundItem = itemRepository.findUserItemByItemId(userId, itemId)
                .orElseThrow(() -> new ItemOfUserNotFoundException(itemId, userId));


        ItemResponseDto response = itemDtoMapper.toDto(foundItem);
        log.info("Successfully retrieved item for user with ID {}: {}", userId, response);
        return response;
    }

    @Override
    public Collection<ItemResponseDto> searchUserItemsBySearchText(long userId, String searchText) {
        if (!userRepository.isUserExist(userId)) {
            throw new UserNotFoundException(userId);
        }

        Collection<Item> foundItems = itemRepository.findUserItemBySearchText(userId, searchText);

        Collection<ItemResponseDto> response = itemDtoMapper.toDto(foundItems);
        log.info("Successfully retrieved search results for user with ID {}: {}", userId, response);
        return response;
    }
}
