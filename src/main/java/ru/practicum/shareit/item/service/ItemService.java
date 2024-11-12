package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {
    ItemSimpleResponseDto createItemByUser(long userId, ItemPostDto itemRequest);

    ItemSimpleResponseDto updateUserItem(long userId, long itemId, ItemPatchDto itemUpdateData);

    Collection<ItemSimpleResponseDto> getOwnerItemsWithComments(long userId);

    ItemWithCommentsAndBookingsResponseDto getItemWithCommentsById(long itemId, long userId);

    Collection<ItemSimpleResponseDto> searchUserItemsBySearchText(long userId, String searchText);

}
