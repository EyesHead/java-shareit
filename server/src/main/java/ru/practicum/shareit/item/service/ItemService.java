package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDetailedResponseDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;

public interface ItemService {
    ItemResponseDto createItemByUser(long userId, ItemPostDto itemRequest);

    ItemResponseDto updateUserItem(long userId, long itemId, ItemPatchDto itemUpdateData);

    Collection<ItemDetailedResponseDto> getOwnerItemsWithComments(long userId);

    ItemDetailedResponseDto getItemWithCommentsById(long itemId, long userId);

    Collection<ItemResponseDto> searchUserItemsBySearchText(long userId, String searchText);

    ItemResponseDto createItemByRequest(long userId, long requestId, ItemPostDto itemCreateDto);
}
