package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.InvalidUpdateDataException;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.util.Constants;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Profile("gateway")
public class ItemGatewayController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItemByUser(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                                   @RequestBody @Valid ItemPostDto itemCreateDto) {
        log.info("[GATEWAY] Received request to create item. UserId = {}, Request data = {}", userId, itemCreateDto);
        return client.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateOwnerItem(@RequestHeader(Constants.USER_ID_HEADER) long ownerId,
                                                  @PathVariable(name = "itemId") long itemId,
                                                  @RequestBody @Valid ItemPatchDto itemUpdateData) {
        log.info("[GATEWAY] Received request to update item. OwnerId = {}, ItemId = {}, Update data = {}", ownerId, itemId, itemUpdateData);

        if (itemUpdateData.getAvailable() == null
                && itemUpdateData.getDescription() == null
                && itemUpdateData.getName() == null) {
            throw new InvalidUpdateDataException("There is nothing to update for item. Id=" + itemId);
        }

        return client.update(ownerId, itemId, itemUpdateData);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerItemsByOwnerId(@RequestHeader(Constants.USER_ID_HEADER) long ownerId) {
        log.info("[GATEWAY] Received request to get items with comments. OwnerId = {}", ownerId);
        return client.getByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsOfOwnerByItemText(@RequestParam(name = "text") String text,
                                                               @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[GATEWAY] Received request to search items. Text = '{}', UserId = {}", text, userId);
        return text.isBlank() ? ResponseEntity.ok(List.of()) : client.getByOwnerIdAndSearchText(userId, text);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemOfOwnerById(@RequestHeader(Constants.USER_ID_HEADER) long ownerId,
                                                     @PathVariable(name = "itemId") long itemId) {
        log.info("[GATEWAY] Received request to get item. OwnerId = {}, ItemId = {}", ownerId, itemId);
        return client.getByIdAndOwnerId(itemId, ownerId);
    }
}