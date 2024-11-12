package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.ControllerValues;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemSimpleResponseDto createItemByUser(@RequestHeader(ControllerValues.USER_ID_HEADER) long userId,
                                                  @Valid @RequestBody ItemPostDto itemCreateDto) {
        log.info("Received request to create item from user. UserId = {}", userId);
        log.debug("Received request to create item with data '{}' for user with UserId = {}", itemCreateDto, userId);

        return itemService.createItemByUser(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemSimpleResponseDto updateOwnerItem(@RequestHeader(ControllerValues.USER_ID_HEADER) long ownerId,
                                                 @PathVariable(name = "itemId") long itemId,
                                                 @Valid @RequestBody ItemPatchDto itemUpdateData) {
        log.info("Received request to update item for owner. ItemId = {}, ownerId = {}", itemId, ownerId);

        return itemService.updateUserItem(ownerId, itemId, itemUpdateData);
    }

    @GetMapping
    public Collection<ItemSimpleResponseDto> getAllOwnerItemsByOwnerId(@RequestHeader(ControllerValues.USER_ID_HEADER) long ownerId) {
        log.info("Received request to get items with comments for owner ID = {}", ownerId);

        return itemService.getOwnerItemsWithComments(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemSimpleResponseDto> searchItemsOfOwnerByItemText(@RequestParam(name = "text") String text,
                                                                          @RequestHeader(ControllerValues.USER_ID_HEADER) long userId) {
        log.info("Received request to search items with text: '{}' for userId: {}", text, userId);

        if (text.isBlank()) {
            return List.of();
        }

        return itemService.searchUserItemsBySearchText(userId, text);
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentsAndBookingsResponseDto getItemOfOwnerById(@RequestHeader(ControllerValues.USER_ID_HEADER) long ownerId,
                                                                     @PathVariable(name = "itemId") long itemId) {
        log.info("Received request to get item by ownerId = {} and itemId = {}", ownerId, itemId);

        return itemService.getItemWithCommentsById(itemId, ownerId);
    }
}