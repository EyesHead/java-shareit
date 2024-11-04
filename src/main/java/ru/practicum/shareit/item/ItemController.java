package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.config.ItemControllerValues;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
    public ItemResponseDto createItemForUser(@RequestHeader(ItemControllerValues.OWNER_ID_HEADER) long userId,
                                             @Valid @RequestBody ItemPostDto itemCreate) {
        log.info("Received request to create item for userId: {}", userId);
        return itemService.createItemForUser(userId, itemCreate);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateUserItem(@RequestHeader(ItemControllerValues.OWNER_ID_HEADER) long userId,
                                          @PathVariable(name = "itemId") long itemId,
                                          @Valid @RequestBody ItemPatchDto itemUpdate) {
        log.info("Received request to update item for userId: {}", userId);

        if (itemUpdate.getName() == null && itemUpdate.getDescription() == null && itemUpdate.getAvailable() == null) {
            log.warn("Nothing to update for user with id = '{}'", userId);
            throw new UnsupportedOperationException("There is nothing to update for user. Id = " + userId);
        }

        return itemService.updateUserItem(userId, itemId, itemUpdate);
    }

    @GetMapping
    public Collection<ItemResponseDto> getItemsForUser(@RequestHeader(ItemControllerValues.OWNER_ID_HEADER) long userId) {
        log.info("Received request to get items for userId: {}", userId);

        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> searchItemsForUser(@RequestParam(name = "text") String text,
                                                          @RequestHeader(ItemControllerValues.OWNER_ID_HEADER) long userId) {
        log.info("Received request to search items with text: '{}' for userId: {}", text, userId);

        if (text.isBlank()) {
            return List.of();
        }

        return itemService.searchUserItemsBySearchText(userId, text);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemOfUserById(@PathVariable(name = "itemId") long itemId,
                                             @RequestHeader(ItemControllerValues.OWNER_ID_HEADER) long userId) {
        log.info("Received request to get item by itemId: {} for userId: {}", itemId, userId);

        return itemService.getUserItemByItemId(userId, itemId);
    }
}