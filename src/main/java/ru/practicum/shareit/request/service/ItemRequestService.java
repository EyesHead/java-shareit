package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestPostDto itemRequestPostDto, long userId);

    Collection<ItemRequestDto> getAllRequestsByUserId(long userId);

    ItemRequestDto getItemRequestByRequestIdAndUserId(long itemRequestId, long userId);
}
