package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(Constants.USER_ID_HEADER) long userId,
                                            @RequestBody @Valid ItemRequestPostDto itemRequestPostDto) {
        return itemRequestService.createRequest(itemRequestPostDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getItemRequestsOfUser(@RequestHeader(Constants.USER_ID_HEADER) long userId) {
        return itemRequestService.getAllRequestsByUserId(userId);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getSpecificItemRequest(@PathVariable(name = "itemRequestId") long itemRequestId,
                                                 @RequestHeader(Constants.USER_ID_HEADER) long userId) {
        return itemRequestService.getItemRequestByRequestIdAndUserId(itemRequestId, userId);
    }
}
