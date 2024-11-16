package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestMapper itemRequestMapper;
    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestPostDto itemRequestPostDto, long userId) {
        return null;
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsByUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return Collections.emptyList();
    }

    @Override
    public ItemRequestDto getItemRequestByRequestIdAndUserId(long itemRequestId, long userId) {
        return null;
    }
}
