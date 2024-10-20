package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;

public interface ItemService {
    ItemResponseDto createItemForUser(long userId, ItemPostDto itemRequest);

    ItemResponseDto updateUserItem(long userId, long itemId, ItemPatchDto itemUpdate);

    Collection<ItemResponseDto> getUserItems(long userId);

    ItemResponseDto getUserItemByItemId(long userId, long itemId);

    Collection<ItemResponseDto> searchUserItemsBySearchText(long userId, String searchText);

}
