package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

import java.util.Collection;

@Mapper(componentModel = "spring", uses = {CommentMapper.class, BookingMapper.class, ItemRequestMapper.class})
public interface ItemMapper {

    Item toEntity(ItemPostDto itemPostDto);

    Item toEntity(ItemPatchDto itemPatchDto);

    ItemResponseDto toResponse(Item item);

    Collection<ItemResponseDto> toResponseList(Collection<Item> items);
}