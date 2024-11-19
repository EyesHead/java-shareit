package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.util.Constants;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Profile("gateway")
public class ItemRequestGatewayController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                                    @RequestBody @Valid ItemRequestPostDto itemRequestPostDto) {
        log.info("[GATEWAY] Create item request for user by userId='{}'", userId);
        return client.createRequest(userId, itemRequestPostDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[GATEWAY] Get all item requests for userId='{}'", userId);
        return client.getUserRequests(userId);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getUserRequestById(@PathVariable(name = "itemRequestId") long requestId,
                                                     @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[GATEWAY] Get item request with requestId='{}' and requesterId='{}'", requestId, userId);
        return client.getUserRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherRequests(@RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[GATEWAY] Get all item requests created by other users for userId='{}'", userId);
        return client.getAllOtherRequests(userId);
    }
}