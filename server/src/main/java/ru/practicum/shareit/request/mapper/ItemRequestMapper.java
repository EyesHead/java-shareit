package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.ResponseOnItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest toEntity(ItemRequestPostDto itemRequestPostDto);

    ItemRequestSimpleDto toSimpleResponse(ItemRequest entity);

    Collection<ItemRequestSimpleDto> toSimpleResponseList(Collection<ItemRequest> entity);

    // Маппинг для отдельного элемотдельного item
    @Mapping(target = "id", source = "id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "name", source = "name")
    ResponseOnItemRequestDto itemToItemResponseShortDto(Item item);

    ItemRequestWithResponsesDto toDetailedResponse(ItemRequest entity);

    Collection<ItemRequestWithResponsesDto> toDetailedResponseList(Collection<ItemRequest> itemRequests);
}