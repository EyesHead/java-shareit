package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemSimpleResponseDto;
import ru.practicum.shareit.item.entity.Item;

import java.util.Collection;

@Mapper(componentModel = "spring", uses = {CommentMapper.class, BookingMapper.class})
public interface ItemMapper {

    Item toItemFromPostDto(ItemPostDto itemPostDto);

    Item toItemFromPatchDto(ItemPatchDto itemPatchDto);

    ItemSimpleResponseDto toResponseList(Item item);

    Collection<ItemSimpleResponseDto> toResponseList(Iterable<Item> item);
}
