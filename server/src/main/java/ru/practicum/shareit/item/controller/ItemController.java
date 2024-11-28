package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDetailedResponseDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Constants;

import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItemByUser(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                            @RequestBody ItemPostDto itemCreateDto) {
        Long requestId = itemCreateDto.getRequestId();
        if (requestId != null) {
            log.info("Received request to create item from user on request. userId='{}', requestId='{}'", userId, requestId);
            return itemService.createItemOnRequest(userId, requestId, itemCreateDto);
        }
        log.info("Received request to create item from user. userId='{}'", userId);
        return itemService.createItemByUser(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateOwnerItem(@RequestHeader(Constants.USER_ID_HEADER) long ownerId,
                                           @PathVariable(name = "itemId") long itemId,
                                           @RequestBody ItemPatchDto itemUpdateData) {
        log.info("Received request to update item for owner. ItemId = {}, ownerId = {}", itemId, ownerId);

        return itemService.updateItemByIdAndOwnerId(ownerId, itemId, itemUpdateData);
    }

    @GetMapping
    public Collection<ItemDetailedResponseDto> getAllOwnerItemsWithComments(@RequestHeader(Constants.USER_ID_HEADER) long ownerId) {
        log.info("Received request to get items with comments for owner ID = {}", ownerId);

        return itemService.getOwnerItemsWithComments(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> searchUserItemsByItemText(@RequestParam(name = "text") String text,
                                                                 @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("Received request to search items with text: '{}' for userId: {}", text, userId);

        return itemService.searchUserItemsBySearchText(userId, text);
    }

    @GetMapping("/{itemId}")
    public ItemDetailedResponseDto getItemOfOwnerById(@RequestHeader(Constants.USER_ID_HEADER) long ownerId,
                                                      @PathVariable(name = "itemId") long itemId) {
        log.info("Received request to get item by ownerId = {} and itemId = {}", ownerId, itemId);

        return itemService.getItemWithCommentsById(itemId, ownerId);
    }
}