package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestSimpleDto createRequest(ItemRequestPostDto itemRequestPostDto, long userId);

    Collection<ItemRequestWithResponsesDto> getUserRequests(long userId);

    ItemRequestWithResponsesDto getByIdAndRequesterId(long itemRequestId, long userId);

    Collection<ItemRequestSimpleDto> getAllRequests();
}
