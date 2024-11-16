package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest toEntity(ItemRequestPostDto itemRequestPostDto);

    ItemRequestDto toResponse(ItemRequest ItemRequestEntity);

    Collection<ItemRequestDto> toResponseList(Collection<ItemRequest> ItemRequestsEntities);
}