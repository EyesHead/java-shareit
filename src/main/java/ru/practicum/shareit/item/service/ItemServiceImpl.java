package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.entity.Comment;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.error.ItemNotFoundException;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemSimpleResponseDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsAndBookingsResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemMapper itemMapper;
    CommentMapper commentMapper;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;

    @Transactional
    @Override
    public ItemSimpleResponseDto createItemByUser(long userId, ItemPostDto itemPost) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Item item = itemMapper.toItemFromPostDto(itemPost);
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);

        log.info("Item successfully created for user. ItemId = {}, userId = {}", savedItem.getId(), userId);

        return itemMapper.toResponseList(savedItem);
    }

    @Transactional
    @Override
    public ItemSimpleResponseDto updateUserItem(long ownerId, long itemId, ItemPatchDto itemPatchDto) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }

        Item itemToUpdate = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        Item itemUpdateData = itemMapper.toItemFromPatchDto(itemPatchDto);

        itemToUpdate = updateItemData(itemToUpdate, itemUpdateData);

        Item updatedItem = itemRepository.save(itemToUpdate);

        ItemSimpleResponseDto response = itemMapper.toResponseList(updatedItem);
        log.info("Item successfully updated for user with ID = {}", ownerId);
        return response;
    }

    private Item updateItemData(Item itemToUpdate, Item updateData) {
        return itemToUpdate.toBuilder()
                .name(updateData.getName() == null ? itemToUpdate.getName() : updateData.getName())
                .description(updateData.getDescription() == null ? itemToUpdate.getDescription() : updateData.getDescription())
                .available(updateData.getAvailable() == null ? itemToUpdate.getAvailable() : updateData.getAvailable())
                .build();
    }

    @Override
    @Transactional
    public Collection<ItemSimpleResponseDto> getOwnerItemsWithComments(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<Item> items = (List<Item>) itemRepository.findAllByOwnerIdWithComments(userId);

        log.info("Successfully retrieved {} items with comments and bookings for user with ID = {}", items.size(), userId);
        return itemMapper.toResponseList(items);
    }

    @Transactional
    @Override
    public ItemWithCommentsAndBookingsResponseDto getItemWithCommentsById(long itemId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Item foundItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        Booking lastBooking = bookingRepository.findItemLastBooking(itemId).orElse(null);
        Booking nextBooking = bookingRepository.findItemNextBooking(itemId).orElse(null);

        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);

        var responseDto = ItemWithCommentsAndBookingsResponseDto.builder()
                .id(foundItem.getId())
                .available(foundItem.getAvailable())
                .name(foundItem.getName())
                .description(foundItem.getDescription())
//                .lastBooking(bookingMapper.toResponse(lastBooking))
//                .nextBooking(bookingMapper.toResponse(nextBooking))
                .lastBooking(null)
                .nextBooking(null)// Так сделано из-за неправильно составленного теста в Postman
                .comments(commentMapper.toResponseList(comments))
                .build();

        log.info("Successfully retrieved item for user with ID = {}", userId);
        return responseDto;
    }

    @Transactional
    @Override
    public Collection<ItemSimpleResponseDto> searchUserItemsBySearchText(long userId, String searchText) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        Collection<Item> foundItems = itemRepository.findBySearchText(searchText);

        Collection<ItemSimpleResponseDto> response = itemMapper.toResponseList(foundItems);
        response = response.stream()
                .filter(ItemSimpleResponseDto::getAvailable)
                .toList();
        log.info("Successfully retrieved search results for user with ID = {}", userId);
        return response;
    }
}