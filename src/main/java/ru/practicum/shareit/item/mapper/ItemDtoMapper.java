package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ItemDtoMapper {
    Item toItem(ItemPostDto itemPostDto);

    Item toItem(ItemPatchDto itemPatchDto);

    ItemResponseDto toDto(Item item);

    Collection<ItemResponseDto> toDto(Collection<Item> items);
}
