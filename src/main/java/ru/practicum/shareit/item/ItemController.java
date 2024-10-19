package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public ItemResponseDto createItemForUser(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody ItemPostDto itemCreate) {
        log.info("Received request to create item for userId: {}", userId);

        ItemResponseDto response = itemService.createItemForUser(userId, itemCreate);

        log.info("Returning response for created item for userId: {}", userId);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateUserItem(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                          @PathVariable(name = "itemId") long itemId,
                                          @Valid @RequestBody ItemPatchDto itemUpdate) {
        log.info("Received request to update item for userId: {}", userId);

        if (itemUpdate.getName() == null && itemUpdate.getDescription() == null && itemUpdate.getAvailable() == null) {
            log.warn("Nothing to update for userId: {}", userId);
            throw new UnsupportedOperationException("There is nothing to update for user " + userId);
        }

        ItemResponseDto response = itemService.updateUserItem(userId, itemId, itemUpdate);

        log.info("Returning response for updated item for userId: {}", userId);
        return response;
    }

    @GetMapping
    public Collection<ItemResponseDto> getItemsForUser(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Received request to get items for userId: {}", userId);

        Collection<ItemResponseDto> response = itemService.getUserItems(userId);

        log.info("Returning response with items for userId: {}", userId);
        return response;
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> searchItemsForUser(@RequestParam(name = "text") String text,
                                                          @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Received request to search items with text: '{}' for userId: {}", text, userId);

        if (text.isBlank()) {
            return List.of();
        }

        Collection<ItemResponseDto> response = itemService.searchUserItemsBySearchText(userId, text);

        log.info("Returning search results for userId: {}", userId);
        return response;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemOfUserById(@PathVariable(name = "itemId") long itemId,
                                             @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Received request to get item by itemId: {} for userId: {}", itemId, userId);

        ItemResponseDto response = itemService.getUserItemByItemId(userId, itemId);

        log.info("Returning response with itemId: {} for userId: {}", itemId, userId);
        return response;
    }
}