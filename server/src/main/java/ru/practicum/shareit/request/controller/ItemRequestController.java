package ru.practicum.shareit.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.dto.ItemRequestSimpleDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
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
    public ItemRequestSimpleDto createItemRequest(
            @RequestHeader(Constants.USER_ID_HEADER) long userId,
            @RequestBody ItemRequestPostDto itemRequestPostDto) {
        log.info("[SERVER | CONTROLLER] createItemRequest called with userId: {}", userId);
        ItemRequestSimpleDto response = itemRequestService.createRequest(itemRequestPostDto, userId);
        log.info("[SERVER | CONTROLLER] createItemRequest completed for userId: {}", userId);
        return response;
    }

    @GetMapping
    public Collection<ItemRequestWithResponsesDto> getUserRequestsWithResponses(
            @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[SERVER | CONTROLLER] getUserRequests called with userId: {}", userId);
        Collection<ItemRequestWithResponsesDto> response = itemRequestService.getUserRequests(userId);
        log.info("[SERVER | CONTROLLER] getUserRequests completed for userId: {}", userId);
        return response;
    }

    @GetMapping("/all")
    public Collection<ItemRequestSimpleDto> getAllRequests() {
        log.info("[SERVER | CONTROLLER] getAllOtherRequests called");
        Collection<ItemRequestSimpleDto> response = itemRequestService.getAllRequests();
        log.info("[SERVER | CONTROLLER] getAllOtherRequests completed with size = {}", response.size());
        return response;
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestWithResponsesDto getSpecificItemRequest(
            @PathVariable(name = "itemRequestId") long itemRequestId,
            @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        log.info("[SERVER | CONTROLLER] getSpecificItemRequest called with itemRequestId: {}", itemRequestId);
        ItemRequestWithResponsesDto response = itemRequestService.getByIdAndRequesterId(itemRequestId, userId);
        log.info("[SERVER | CONTROLLER] getSpecificItemRequest completed for itemRequestId: {}", itemRequestId);
        return response;
    }
}