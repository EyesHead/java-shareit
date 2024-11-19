package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestWithItemsResponseDto createRequest(ItemRequestPostDto itemRequestPostDto, long userId);

    Collection<ItemRequestWithItemsResponseDto> getUserRequests(long userId);

    ItemRequestWithItemsResponseDto getByIdAndRequesterId(long itemRequestId, long userId);

    Collection<ItemRequestWithItemsResponseDto> getAllOtherRequests(long userId);
}
