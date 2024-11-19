package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemResponseShortDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDto;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    // Маппинг для отдельного элемотдельного item
    @Mapping(target = "id", source = "id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "name", source = "name")
    ItemResponseShortDto itemToItemResponseShortDto(Item item);

    ItemRequestWithItemsResponseDto toResponse(ItemRequest entity);

    Collection<ItemRequestWithItemsResponseDto> toResponseList(Collection<ItemRequest> itemRequests);

    ItemRequest toEntity(ItemRequestPostDto itemRequestPostDto);
}