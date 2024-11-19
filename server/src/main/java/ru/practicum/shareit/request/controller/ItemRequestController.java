package ru.practicum.shareit.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.Constants;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestWithItemsResponseDto createItemRequest(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                                             @RequestBody ItemRequestPostDto itemRequestPostDto) {
        log.info("[SERVER | CONTROLLER] createItemRequest called with userId: {}", userId);
        ItemRequestWithItemsResponseDto response = itemRequestService.createRequest(itemRequestPostDto, userId);
        log.info("[SERVER | CONTROLLER] createItemRequest completed for userId: {}", userId);
        return response;
    }

    @GetMapping
    public Collection<ItemRequestWithItemsResponseDto> getUserRequests(@RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[SERVER | CONTROLLER] getUserRequests called with userId: {}", userId);
        Collection<ItemRequestWithItemsResponseDto> response = itemRequestService.getUserRequests(userId);
        log.info("[SERVER | CONTROLLER] getUserRequests completed for userId: {}", userId);
        return response;
    }

    @GetMapping("/all")
    public Collection<ItemRequestWithItemsResponseDto> getAllOtherRequests(@RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[SERVER | CONTROLLER] getAllOtherRequests called with userId: {}", userId);
        Collection<ItemRequestWithItemsResponseDto> response = itemRequestService.getAllOtherRequests(userId);
        log.info("[SERVER | CONTROLLER] getAllOtherRequests completed for userId: {}", userId);
        return response;
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestWithItemsResponseDto getSpecificItemRequest(@PathVariable(name = "itemRequestId") long itemRequestId,
                                                                  @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[SERVER | CONTROLLER] getSpecificItemRequest called with itemRequestId: {}", itemRequestId);
        ItemRequestWithItemsResponseDto response = itemRequestService.getByIdAndRequesterId(itemRequestId, userId);
        log.info("[SERVER | CONTROLLER] getSpecificItemRequest completed for itemRequestId: {}", itemRequestId);
        return response;
    }
}